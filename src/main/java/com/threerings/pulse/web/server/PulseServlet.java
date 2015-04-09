//
// $Id$

package com.threerings.pulse.web.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import com.samskivert.servlet.util.ParameterUtil;
import com.samskivert.util.Calendars;
import com.samskivert.util.StringUtil;

import com.threerings.pulse.server.persist.PulseRecord;
import com.threerings.pulse.server.persist.PulseRepository;

/**
 * Displays our pulse datasets via a simple web interface. This servlet must have its dependencies
 * injected.
 */
@Singleton
public class PulseServlet extends HttpServlet
{
    /** Used in our template (and therefore must be public, sigh). */
    public static class GraphData
    {
        public final RecordInfo.FieldInfo field;

        public GraphData (RecordInfo.FieldInfo field, String server,
                          Collection<PulseRecord> records) {
            this.field = field;

            _server = server;
            _data = new Number[records.size()];

            int ll = records.size(), idx = 0, didx = 0;
            int[] ridxs = new int[] { 0, ll/5, 2*ll/5, 3*ll/5, 4*ll/5, ll-1 };
            _ylbls = new String[ridxs.length];
            for (PulseRecord record : records) {
                if (idx == ridxs[didx]) {
                    _ylbls[didx++] = _yfmt.format(record.recorded);
                }
                _data[idx++] = field.getValue(record);
            }
            _max = normalize(_data);
        }

        public String getChartParams () {
            StringBuilder buf = new StringBuilder();
            String legend = _server + " " + field.getName();
            int width = _data.length + EXTRA_WIDTH + CHAR_WIDTH * legend.length() +
                CHAR_WIDTH + String.valueOf(_max).length();
            buf.append("chs=").append(width).append("x").append(HEIGHT); // size
            buf.append("&cht=").append("lc"); // line chart
            buf.append("&chdl=").append(legend); // legend
            buf.append("&chxt=x,y"); // axes
            buf.append("&chg=20,100"); // grid
            buf.append("&chxr=1,0,").append(_max); // y axis range
            buf.append("&chxl=0:|").append(StringUtil.join(_ylbls, "|"));
            buf.append("&chd=e:").append(encode(_data)); // our data
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
            InputStream in = getClass().getClassLoader().getResourceAsStream(GRAPHS_TMPL);
            if (in == null) throw new FileNotFoundException(GRAPHS_TMPL);
            _template = _compiler.compile(new InputStreamReader(in, "UTF-8"));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override // from HttpServlet
    protected void doGet (HttpServletRequest req, HttpServletResponse rsp)
        throws IOException
    {
        Map<String,Object> ctx = createContext(req);
        ctx.put("graphs", Lists.newArrayList());
        sendResponse(ctx, rsp);
    }

    @Override // from HttpServlet
    protected void doPost (HttpServletRequest req, HttpServletResponse rsp)
        throws IOException
    {
        Map<String,Object> ctx = createContext(req);
        Timestamp start = Calendars.now().addDays(-1).toTimestamp(); // TODO

        List<GraphData> graphs = Lists.newArrayList();
        ctx.put("graphs", graphs);

        for (RecordInfo info : _records) {
            Multimap<String, PulseRecord> data = null;
            for (RecordInfo.FieldInfo field : info.fields) {
                if (ParameterUtil.isSet(req, field.getId())) {
                    if (data == null) {
                        data = ArrayListMultimap.create();
                        for (PulseRecord record : _pulseRepo.loadPulseHistory(info.clazz, start)) {
                            data.put(record.server, record);
                        }
                    }
                    for (String server : Sets.newTreeSet(data.keySet())) {
                        graphs.add(new GraphData(field, server, data.get(server)));
                    }
                }
            }
        }

        sendResponse(ctx, rsp);
    }

    protected Map<String,Object> createContext (final HttpServletRequest req)
    {
        Map<String,Object> ctx = new HashMap<String,Object>();
        ctx.put("records", _records);
        ctx.put("checked", new Mustache.Lambda() {
            public void execute (Template.Fragment frag, Writer out) throws IOException {
                String key = frag.execute();
                if (req.getParameter(key) != null) out.write("checked");
            }
        });
        return ctx;
    }

    protected void sendResponse (Map<String,Object> ctx, HttpServletResponse rsp)
        throws IOException
    {
        PrintWriter out = new PrintWriter(rsp.getOutputStream());
        _template.execute(ctx, out);
        out.close();
    }

    protected static long normalize (Number[] data)
    {
        double max = 0;
        for (Number value : data) {
            max = Math.max(value.doubleValue(), max);
        }
        if (max > 0) {
            for (int ii = 0; ii < data.length; ii++) {
                data[ii] = (int)(SCALED_MAX * data[ii].doubleValue() / max);
            }
        }
        return (long)max;
    }

    protected static String encode (Number[] data)
    {
        StringBuilder buf = new StringBuilder();
        for (Number num : data) {
            int value = num.intValue();
            if (value < 0) {
                buf.append("__");
            } else {
                buf.append(EENC_CHARS.charAt(value / EENC_CHARS.length()));
                buf.append(EENC_CHARS.charAt(value % EENC_CHARS.length()));
            }
        }
        return buf.toString();
    }

    protected final List<RecordInfo> _records = Lists.newArrayList();
    protected final Mustache.Compiler _compiler = Mustache.compiler();
    protected Template _template;

    @Inject protected PulseRepository _pulseRepo;

    protected static SimpleDateFormat _yfmt = new SimpleDateFormat("HH:mm");

    protected static final String GRAPHS_TMPL = "com/threerings/pulse/web/server/graphs.tmpl";
    protected static final int SCALED_MAX = 4095;
    protected static final int EXTRA_WIDTH = 25; // for niggling bits
    protected static final int CHAR_WIDTH = 9; // for axis labels and key
    protected static final int HEIGHT = 100; // chart height
    protected static final String EENC_CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-.";
}
