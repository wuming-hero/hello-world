package com.wuming.model;

/**
 * @author manji
 * Created on 2024/3/6 10:16
 */
public class Node {

    private final int weight;  // 初始权重 （保持不变）
    private final String serverName; // 服务名

    private int currentWeight; // 当前权重

    public Node(String serverName, int weight) {
        this.serverName = serverName;
        this.weight = weight;
        this.currentWeight = weight;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public int getWeight() {
        return weight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public String getServerName() {
        return serverName;
    }

}
