package com.wuming.view.code;

import org.apache.poi.ss.formula.functions.T;

/**
 * @author che
 * Created on 2025/5/30 16:51
 */
public interface Iterator<T> {

    public T next();

    public boolean hasNext();
}
