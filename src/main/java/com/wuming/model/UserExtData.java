package com.wuming.model;

import java.io.Serializable;

/**
 * excel 表头列数据，当前表格只有 _co 一列
 * @author manji
 * Created on 2023/10/26 16:54
 */
public class UserExtData implements Serializable {

    private String _c0;

    public String get_c0() {
        return _c0;
    }

    public void set_c0(String _c0) {
        this._c0 = _c0;
    }

}
