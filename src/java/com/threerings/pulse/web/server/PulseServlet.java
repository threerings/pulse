//
// $Id$

package com.threerings.pulse.web.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.samskivert.depot.PersistentRecord;
import com.samskivert.velocity.VelocityUtil;

import com.threerings.pulse.server.persist.PulseRecord;
import com.threerings.pulse.server.persist.PulseRepository;

import static com.threerings.pulse.Log.log;

/**
 * Displays our pulse datasets via a simple web interface. This servlet must have its dependencies
 * injected.
 */
@Singleton
public class PulseServlet extends HttpServlet
{
    @Override // from HttpServlet
    public void init ()
        throws ServletException
    {
        super.init();

        for (Class<? extends PersistentRecord> rclass : _pulseRepo.getPulseRecords()) {
            RecordInfo info = new RecordInfo();
            info.name = rclass.getSimpleName();
            for (Field field : rclass.getFields()) {
                if (Modifier.isPublic(field.getModifiers()) &&
                    !Modifier.isStatic(field.getModifiers())) {
                    info.fields.add(field.getName());
                }
            }
            _records.add(info);
        }

        try {
            _velocity = VelocityUtil.createEngine();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize Velocity", e);
        }
    }

    @Override // from HttpServlet
    protected void doGet (HttpServletRequest req, HttpServletResponse rsp)
        throws IOException
    {
        VelocityContext ctx = new VelocityContext();
        ctx.put("records", _records);

        PrintWriter out = new PrintWriter(rsp.getOutputStream());
        try {
            _velocity.mergeTemplate(GRAPHS_TMPL, "UTF-8", ctx, out);
            out.close();
        } catch (Exception e) {
            throw new IOException("Velocity failure", e);
        }
    }

    protected static class RecordInfo
    {
        public String name;
        public List<String> fields = Lists.newArrayList();
    }

    protected List<RecordInfo> _records = Lists.newArrayList();
    protected VelocityEngine _velocity;

    @Inject protected PulseRepository _pulseRepo;

    protected static final String GRAPHS_TMPL = "com/threerings/pulse/web/server/graphs.tmpl";
}
