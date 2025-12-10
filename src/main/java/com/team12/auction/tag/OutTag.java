package com.team12.auction.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;
import java.io.Writer;

public class OutTag extends SimpleTagSupport {
    private Object value;
    private boolean escapeXml = true;

    public void setValue(Object value) {
        this.value = value;
    }

    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (value == null) {
            return;
        }

        Writer out = getJspContext().getOut();
        String text = value.toString();
        out.write(escapeXml ? escape(text) : text);
    }

    private String escape(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            switch (c) {
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                case '&' -> sb.append("&amp;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
