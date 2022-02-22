package org.innovateuk.ifs.starters.stubdev.filter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * Used to wrap the response so that it can be edited.
 */
public class CharResponseWrapper extends HttpServletResponseWrapper {
    private CharArrayWriter writer;

    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
        writer = new CharArrayWriter();
    }

    public PrintWriter getWriter() {
        return new PrintWriter(writer);
    }

    public String toString() {
        return writer.toString();
    }

}