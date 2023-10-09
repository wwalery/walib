package dev.walgo.walib.db;

import org.apache.commons.lang3.StringUtils;

class DBUtils {

    private DBUtils() {
        // do nothing
    }

    static String maskPattern(String pattern) {
        return pattern != null ? StringUtils.replace(pattern, "_", "\\_") : null;
    }

}
