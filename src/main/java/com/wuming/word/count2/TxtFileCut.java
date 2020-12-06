package com.wuming.word.count2;

/**
 * @author wuming
 * Created on 2020-12-05 22:06
 */
public class TxtFileCut extends TxtFile {

    /**
     * 文件切片的开始节点
     */
    private long startPoint;
    /**
     * 文件切片的结束节点
     */
    @Deprecated
    private long endPoint;
    /**
     * 切片大小
     */
    private long size;

    public TxtFileCut() {
    }

    public TxtFileCut(String fileName, String publishTime, long startPoint, long size) {
        this.setFileName(fileName);
        this.setPublishTime(publishTime);
        this.startPoint = startPoint;
        this.size = size;
    }

    public long getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(long startPoint) {
        this.startPoint = startPoint;
    }

    public long getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(long endPoint) {
        this.endPoint = endPoint;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "TxtFileCut{" +
                "fileName=" + this.getFileName() +
                ",publishTime=" + this.getPublishTime() +
                ",startPoint=" + startPoint +
                ", size=" + size +
                '}';
    }
}
