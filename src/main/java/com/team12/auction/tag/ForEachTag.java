package com.team12.auction.tag;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class ForEachTag extends SimpleTagSupport {
    private Object items;
    private String var;

    public void setItems(Object items) {
        this.items = items;
    }

    public void setVar(String var) {
        this.var = var;
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (items == null || var == null || var.isEmpty()) {
            return;
        }

        Iterator<?> iterator = toIterator(items);
        if (iterator == null) {
            return;
        }

        JspContext context = getJspContext();
        while (iterator.hasNext()) {
            Object value = iterator.next();
            context.setAttribute(var, value);
            if (getJspBody() != null) {
                getJspBody().invoke(null);
            }
        }
    }

    private Iterator<?> toIterator(Object source) {
        if (source instanceof Collection<?>) {
            return ((Collection<?>) source).iterator();
        }
        if (source instanceof Map<?, ?> map) {
            return map.entrySet().iterator();
        }
        if (source != null && source.getClass().isArray()) {
            return new Iterator<>() {
                private int index = 0;
                private final int length = Array.getLength(source);

                @Override
                public boolean hasNext() {
                    return index < length;
                }

                @Override
                public Object next() {
                    return Array.get(source, index++);
                }
            };
        }
        return null;
    }
}
