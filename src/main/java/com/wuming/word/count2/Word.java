//package com.wuming.word.count;
//
//import java.util.concurrent.atomic.AtomicLong;
//
//public class Word implements Comparable<Word> {
//
//    /**
//     * 单词
//     */
//    private String text;
//    /**
//     * 出现的次数
//     */
////    private int times;
//
//    private AtomicLong times;
//
//
//
//    public Word(String text, int times) {
//        this.text = text;
//        this.times = times;
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public int getTimes() {
//        return times;
//    }
//
//    public void setTimes(int times) {
//        this.times = times;
//    }
//
//    @Override
//    public int compareTo(Word a) {
//        return (-1) * (this.getTimes() - a.getTimes());
//    }
//
//}
