//
// $Id$

package com.threerings.pulse.web.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.samskivert.servlet.util.ParameterUtil;
import com.samskivert.util.StringUtil;
import com.samskivert.velocity.FormTool;
import com.samskivert.velocity.VelocityUtil;

import com.threerings.pulse.server.PulseManager;
import com.threerings.pulse.server.persist.PulseRecord;
import com.threerings.pulse.server.persist.PulseRepository;
import com.threerings.pulse.util.PulseUtil;

import static com.threerings.pulse.Log.log;

/**
 * Displays our pulse datasets via a simple web interface. This servlet must have its dependencies
 * injected.
 */
@Singleton
public class PulseServlet extends HttpServlet
{
    /** Used in our template (and therefore must be public, sigh). */
    public static class RecordInfo
    {
        public class FieldInfo {
            public final Field field;

            public FieldInfo (Field field) {
                this.field = field;
            }

            public String getName () {
                return field.getName();
            }

            public String getId () {
                return clazz.getSimpleName() + "." + getName();
            }

            public Number getValue (PulseRecord record) {
                try {
                    return (Number)field.get(record);
                } catch (Exception e) {
                    log.warning("Failed to fetch " + getId() + " from " + record, e);
                    return 0;
                }
            }
        }

        public final Class<? extends PulseRecord> clazz;
        public final List<FieldInfo> fields = Lists.newArrayList();

        public RecordInfo (Class<? extends PulseRecord> clazz) {
            this.clazz = clazz;

            for (Field field : clazz.getFields()) {
                if (Modifier.isPublic(field.getModifiers()) &&
                    !Modifier.isStatic(field.getModifiers()) &&
                    !field.getDeclaringClass().equals(PulseRecord.class)) {
                    fields.add(new FieldInfo(field));
                }
            }
        }

        public String getName () {
            return clazz.getSimpleName();
        }
    }

    /** Used in our template (and therefore must be public, sigh). */
    public static class GraphData
    {
        public final RecordInfo.FieldInfo field;

        public GraphData (long start, RecordInfo.FieldInfo field, String server,
                          Collection<PulseRecord> records) {
            this.field = field;

            _server = server;
            _data = new Number[records.size()];
            _ylbls = new String[records.size()];

            Calendar cal = Calendar.getInstance();
            int hour = -1, idx = 0;
            for (PulseRecord record : records) {
                _data[idx] = field.getValue(record);
                cal.setTime(record.recorded);
                int rhour = cal.get(Calendar.HOUR_OF_DAY);
                if (rhour > hour+1) {
                    _ylbls[idx] = _yfmt.format(record.recorded);
                    hour = rhour;
                } else {
                    _ylbls[idx] = "";
                }
                idx++;
            }
            _max = normalize(_data);
        }

        public String getChartParams () {
            StringBuilder buf = new StringBuilder();
            buf.append("chs=").append(_data.length+EXTRA_WIDTH).append("x").append(100); // size
            buf.append("&cht=").append("lc"); // line chart
            buf.append("&chtt=").append(_server); // title
            buf.append("&chxt=x,y"); // axes
            buf.append("&chxr=1,0,").append(_max); // y axis range
            buf.append("&chxl=0:|").append(StringUtil.join(_ylbls, "|"));
            buf.append("&chd=t:").append(StringUtil.join(_data, ",")); // our data
            return buf.toString();
        }

        protected final String _server;
        protected final Number[] _data;
        protected final long _max;
        protected final String[] _ylbls;
    }

    @Override // from HttpServlet
    public void init ()
        throws ServletException
    {
        super.init();

        for (Class<? extends PulseRecord> rclass : _pulseRepo.getPulseRecords()) {
            _records.add(new RecordInfo(rclass));
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
        VelocityContext ctx = createContext(req);
        ctx.put("graphs", Lists.newArrayList());

        sendResponse(ctx, rsp);
    }

    @Override // from HttpServlet
    protected void doPost (HttpServletRequest req, HttpServletResponse rsp)
        throws IOException
    {
        VelocityContext ctx = createContext(req);
        int days = 0; // TODO
        long start = PulseUtil.getStart(days);

        List<GraphData> graphs = Lists.newArrayList();
        ctx.put("graphs", graphs);

        for (RecordInfo info : _records) {
            Multimap<String, PulseRecord> data = null;
            for (RecordInfo.FieldInfo field : info.fields) {
                if (ParameterUtil.isSet(req, field.getId())) {
                    if (data == null) {
                        data = ArrayListMultimap.create();
                        for (PulseRecord record : _pulseRepo.loadPulseHistory(info.clazz, days)) {
                            data.put(record.server, record);
                        }
                    }
                    for (String server : Sets.newTreeSet(data.keySet())) {
                        graphs.add(new GraphData(start, field, server, data.get(server)));
                    }
                }
            }
        }

        sendResponse(ctx, rsp);
    }

    protected VelocityContext createContext (HttpServletRequest req)
    {
        VelocityContext ctx = new VelocityContext();
        ctx.put("records", _records);
        ctx.put("form", new FormTool(req));
        return ctx;
    }

    protected void sendResponse (VelocityContext ctx, HttpServletResponse rsp)
        throws IOException
    {
        PrintWriter out = new PrintWriter(rsp.getOutputStream());
        try {
            _velocity.mergeTemplate(GRAPHS_TMPL, "UTF-8", ctx, out);
            out.close();
        } catch (Exception e) {
            throw new IOException("Velocity failure", e);
        }
    }

    protected static long normalize (Number[] data)
    {
        long max = 0;
        for (Number value : data) {
            max = Math.max(value.longValue(), max);
        }
        if (max > 0) {
            for (int ii = 0; ii < data.length; ii++) {
                data[ii] = (int)(SCALED_MAX * data[ii].longValue() / max);
            }
        }
        return max;
    }

    protected List<RecordInfo> _records = Lists.newArrayList();
    protected VelocityEngine _velocity;

    @Inject protected PulseRepository _pulseRepo;

    protected static SimpleDateFormat _yfmt = new SimpleDateFormat("HH:mm");

    protected static final String GRAPHS_TMPL = "com/threerings/pulse/web/server/graphs.tmpl";
    protected static final int SCALED_MAX = 100;
    protected static final int EXTRA_WIDTH = 50; // for axis labels and small bits
}
