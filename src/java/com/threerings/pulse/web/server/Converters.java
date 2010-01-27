package com.threerings.pulse.web.server;

import com.google.common.base.Function;

public class Converters
{
    public static final Function<String, Long> TO_LONG = new Function<String, Long>() {
        public Long apply (String from) {
            return Long.parseLong(from);
        }
    };

    public static final Function<String, Boolean> TO_BOOLEAN = new Function<String, Boolean>() {
        public Boolean apply (String from) {
            return Boolean.parseBoolean(from);
        }
    };
}
