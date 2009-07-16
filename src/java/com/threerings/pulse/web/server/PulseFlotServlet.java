//
// $Id$

package com.threerings.pulse.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONException;
import org.json.JSONWriter;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import com.samskivert.io.StreamUtil;
import com.samskivert.servlet.util.ParameterUtil;
import com.samskivert.velocity.VelocityUtil;

import com.threerings.pulse.server.persist.PulseRecord;
import com.threerings.pulse.server.persist.PulseRepository;

/**
 * Displays our pulse datasets via flot charts. This servlet must have its dependencies injected.
 */
public class PulseFlotServlet extends HttpServlet
{
    @Override // from HttpServlet
    public void init ()
        throws ServletException
    {
        for (Class<? extends PulseRecord> rclass : _pulseRepo.getPulseRecords()) {
            _records.put(rclass.getSimpleName(), new RecordInfo(rclass));
        }

        try {
            _velocity = VelocityUtil.createEngine();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize Velocity", e);
        }
    }

    @Override // from HttpServlet
    protected void doGet (HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
        String path = req.getPathInfo();
        if (path.length() > 1) {
            // If anything other than / is requested, try to serve up a resource from the classpath
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            InputStream in = contextLoader.getResourceAsStream(RSRC_BASE + path);
            if (in != null) {
                // It'd be nice if there were some exposed mimetype database in Java, but I guess
                // this will do
                if (path.endsWith(".css")) {
                    resp.setContentType("text/css");
                } else if (path.endsWith(".js")) {
                    resp.setContentType("text/javascript");
                } else {
                    log("Unknown content type for " + path);
                }
                StreamUtil.copy(in, resp.getOutputStream());
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            String recordName = ParameterUtil.getParameter(req, "record", true);
            if (recordName == null) {
                sendIndexPage(resp);
            } else {
                sendJsonRecords(resp, recordName, ParameterUtil.getParameter(req, "field", true));
            }
        }
    }

    /**
     * Writes the index page for flot graphs to <code>resp</code>.
     */
    protected void sendIndexPage (HttpServletResponse resp)
        throws IOException
    {
        resp.setContentType("text/html");
        StringWriter writer = new StringWriter();
        JSONWriter jsonWriter = new JSONWriter(writer);

        try {
            jsonWriter.object();
            for (RecordInfo info : _records.values()) {
                jsonWriter.key(info.clazz.getSimpleName()).array();
                for (RecordInfo.FieldInfo field : info.fields) {
                    jsonWriter.value(field.getName());
                }
                jsonWriter.endArray();
            }
            jsonWriter.endObject();
        } catch (JSONException je) {
            throw new IllegalStateException("Could not convert events to json.", je);
        }
        VelocityContext ctx = new VelocityContext();
        ctx.put("records", writer.toString());

        try {
            _velocity.mergeTemplate(GRAPHS_TMPL, "UTF-8", ctx, resp.getWriter());
        } catch (Exception e) {
            throw (IOException)new IOException("Velocity failure").initCause(e); // yay 1.5!
        }
    }

    /**
     * Writes a JSON object for values of the given field from the given record to the response.
     * The object contains a field <code>records</code>, which contains an Array of Arrays of
     * server name, pulse time and record value.
     */
    protected void sendJsonRecords (HttpServletResponse resp, String recordName, String fieldName)
        throws IOException
    {
        RecordInfo info = _records.get(recordName);
        RecordInfo.FieldInfo field = info.getField(fieldName);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        JSONWriter json = new JSONWriter(resp.getWriter());
        int days = 1; // TODO
        try {
            json.object().key("records").array();
            for (PulseRecord record : _pulseRepo.loadPulseHistory(info.clazz, days)) {
                json.array();
                json.value(record.server);
                // Break Timestamps out into a long so it's directly usable from
                // JavaScript, otherwise it's stringified.
                json.value(record.recorded.getTime());
                json.value(field.getValue(record));
                json.endArray();
            }
            json.endArray().endObject();
        } catch (JSONException je) {
            throw new IllegalStateException("Could not convert events to json.", je);
        }
    }

    protected VelocityEngine _velocity;
    protected Map<String, RecordInfo> _records = Maps.newHashMap();

    @Inject protected PulseRepository _pulseRepo;

    protected static final String RSRC_BASE = "com/threerings/pulse/web/server/rsrc";
    protected static final String GRAPHS_TMPL = "com/threerings/pulse/web/server/flot_graphs.tmpl";
}
