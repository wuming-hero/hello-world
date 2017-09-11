package com.wuming.util;

import com.google.common.base.Joiner;

/**
 * 常用连接符
 * 配合guava的Joiner使用
 */
public class Joiners {
    public static final Joiner DOT = Joiner.on(".").skipNulls();
    public static final Joiner COMMA = Joiner.on(",").skipNulls();
    public static final Joiner COLON = Joiner.on(":").skipNulls();
}