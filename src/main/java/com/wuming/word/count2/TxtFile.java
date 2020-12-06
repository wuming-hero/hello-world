package com.wuming.word.count2;

import java.io.Serializable;

/**
 * @author wuming
 * Created on 2020-12-05 22:05
 */
public class TxtFile  implements Serializable {

    /**
     * 文件名
     */
    private String fileName;
    /**
     * 出版日期
     */
    private String publishTime;

    public TxtFile() {
    }

    public TxtFile(String fileName) {
        this.fileName = fileName;
        this.publishTime = "2020-12-06";
    }

    public TxtFile(String fileName, String publishTime) {
        this.fileName = fileName;
        this.publishTime = publishTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public String toString() {
        return "TxtFile{" +
                "fileName='" + fileName + '\'' +
                ", publishTime='" + publishTime + '\'' +
                '}';
    }
}
