package com.threerings.pulse.jetty.server;

import java.util.Locale;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Wraps an HttpServletResponse and records the amount of time spent writing to its OutputStream
 * or PrintWriter.
 */
public class IOTimingResponse extends HttpServletResponseWrapper
{
    public IOTimingResponse (HttpServletResponse response)
    {
        super(response);
    }

    /**
     * Returns the number of milliseconds spent in writing to the response thus far.
     */
    public long getIODuration ()
    {
        return _ioTime;
    }

    @Override
    public PrintWriter getWriter ()
        throws IOException
    {
        if (_writer == null) {
            _writer = new TimingPrintWriter(super.getWriter());
        }
        return _writer;
    }

    @Override
    public ServletOutputStream getOutputStream ()
        throws IOException
    {
        if (_out == null) {
            _out = new TimingServletOutputStream(super.getOutputStream());
        }
        return _out;
    }

    protected class TimingServletOutputStream extends ServletOutputStream
    {
        public TimingServletOutputStream (ServletOutputStream base)
        {
            _base = base;
        }
        @Override public void close () throws IOException {
            long start = System.currentTimeMillis();
            _base.close();
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void flush () throws IOException {
            long start = System.currentTimeMillis();
            _base.flush();
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (boolean arg0) throws IOException {
            long start = System.currentTimeMillis();
            _base.print(arg0);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (char c) throws IOException {
            long start = System.currentTimeMillis();
            _base.print(c);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (double d) throws IOException {
            long start = System.currentTimeMillis();
            _base.print(d);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (float f) throws IOException {
            long start = System.currentTimeMillis();
            _base.print(f);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (int i) throws IOException {
            long start = System.currentTimeMillis();
            _base.print(i);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (long l) throws IOException {
            long start = System.currentTimeMillis();
            _base.print(l);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (String arg0) throws IOException {
            long start = System.currentTimeMillis();
            _base.print(arg0);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println () throws IOException {
            long start = System.currentTimeMillis();
            _base.println();
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (boolean b) throws IOException {
            long start = System.currentTimeMillis();
            _base.println(b);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (char c) throws IOException {
            long start = System.currentTimeMillis();
            _base.println(c);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (double d) throws IOException {
            long start = System.currentTimeMillis();
            _base.println(d);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (float f) throws IOException {
            long start = System.currentTimeMillis();
            _base.println(f);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (int i) throws IOException {
            long start = System.currentTimeMillis();
            _base.println(i);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (long l) throws IOException {
            long start = System.currentTimeMillis();
            _base.println(l);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (String s) throws IOException {
            long start = System.currentTimeMillis();
            _base.println(s);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void write (byte[] b, int off, int len) throws IOException {
            long start = System.currentTimeMillis();
            _base.write(b, off, len);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void write (byte[] b) throws IOException {
            long start = System.currentTimeMillis();
            _base.write(b);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void write (int b) throws IOException {
            long start = System.currentTimeMillis();
            _base.write(b);
            _ioTime += System.currentTimeMillis() - start;
        }

        protected final ServletOutputStream _base;
    }

    protected class TimingPrintWriter extends PrintWriter
    {
        public TimingPrintWriter(PrintWriter base) {
            super(base);
            _base = base;
        }

        @Override public PrintWriter append (char c) {
            long start = System.currentTimeMillis();
            _base.append(c);
            _ioTime += System.currentTimeMillis() - start;
            return this;
        }

        @Override public PrintWriter append (CharSequence csq, int start, int end) {
            long timerStart = System.currentTimeMillis();
            _base.append(csq, start, end);
            _ioTime += System.currentTimeMillis() - timerStart;
            return this;
        }

        @Override public PrintWriter append (CharSequence csq) {
            long start = System.currentTimeMillis();
            _base.append(csq);
            _ioTime += System.currentTimeMillis() - start;
            return this;
        }

        @Override public boolean checkError () {
            long start = System.currentTimeMillis();
            boolean result = _base.checkError();
            _ioTime += System.currentTimeMillis() - start;
            return result;
        }

        @Override public PrintWriter format (Locale l, String format, Object... args) {
            long start = System.currentTimeMillis();
            _base.format(l, format, args);
            _ioTime += System.currentTimeMillis() - start;
            return this;
        }

        @Override public PrintWriter format (String format, Object... args) {
            long start = System.currentTimeMillis();
            _base.format(format, args);
            _ioTime += System.currentTimeMillis() - start;
            return this;
        }

        @Override public PrintWriter printf (Locale l, String format, Object... args) {
            long start = System.currentTimeMillis();
            _base.printf(l, format, args);
            _ioTime += System.currentTimeMillis() - start;
            return this;
        }

        @Override public PrintWriter printf (String format, Object... args) {
            long start = System.currentTimeMillis();
            _base.printf(format, args);
            _ioTime += System.currentTimeMillis() - start;
            return this;
        }

        @Override public void close () {
            long start = System.currentTimeMillis();
            _base.close();
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void flush () {
            long start = System.currentTimeMillis();
            _base.flush();
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (boolean b) {
            long start = System.currentTimeMillis();
            _base.print(b);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (char c) {
            long start = System.currentTimeMillis();
            _base.print(c);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (char[] s) {
            long start = System.currentTimeMillis();
            _base.print(s);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (double d) {
            long start = System.currentTimeMillis();
            _base.print(d);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (float f) {
            long start = System.currentTimeMillis();
            _base.print(f);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (int i) {
            long start = System.currentTimeMillis();
            _base.print(i);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (long l) {
            long start = System.currentTimeMillis();
            _base.print(l);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (Object obj) {
            long start = System.currentTimeMillis();
            _base.print(obj);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void print (String s) {
            long start = System.currentTimeMillis();
            _base.print(s);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println () {
            long start = System.currentTimeMillis();
            _base.println();
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (boolean x) {
            long start = System.currentTimeMillis();
            _base.println(x);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (char x) {
            long start = System.currentTimeMillis();
            _base.println(x);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (char[] x) {
            long start = System.currentTimeMillis();
            _base.println(x);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (double x) {
            long start = System.currentTimeMillis();
            _base.println(x);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (float x) {
            long start = System.currentTimeMillis();
            _base.println(x);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (int x) {
            long start = System.currentTimeMillis();
            _base.println(x);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (long x) {
            long start = System.currentTimeMillis();
            _base.println(x);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (Object x) {
            long start = System.currentTimeMillis();
            _base.println(x);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void println (String x) {
            long start = System.currentTimeMillis();
            _base.println(x);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void write (char[] buf, int off, int len) {
            long start = System.currentTimeMillis();
            _base.write(buf, off, len);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void write (char[] buf) {
            long start = System.currentTimeMillis();
            _base.write(buf);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void write (int c) {
            long start = System.currentTimeMillis();
            _base.write(c);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void write (String s, int off, int len) {
            long start = System.currentTimeMillis();
            _base.write(s, off, len);
            _ioTime += System.currentTimeMillis() - start;
        }

        @Override public void write (String s) {
            long start = System.currentTimeMillis();
            _base.write(s);
            _ioTime += System.currentTimeMillis() - start;
        }

        protected final PrintWriter _base;
    }

    protected ServletOutputStream _out;

    protected PrintWriter _writer;

    protected long _ioTime;
}
