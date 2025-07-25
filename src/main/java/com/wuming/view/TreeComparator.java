package com.wuming.view;

import com.google.common.collect.ImmutableMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 * 给定两棵二叉树的根节点 oldTree 和 newTree，每个树节点包含如下属性：
 * id (唯一标识符，整型，全局唯一)
 * name (操作名称，字符串，如 "risk_check")
 * params (操作参数，字典，如 {"timeout": 5})
 *
 * 变更比较规则
 * 1. 节点位置变化但 id 相同视为修改（需记录旧参数和新参数）
 * 2. 节点 id 在旧树存在但新树不存在 → 属于删除
 * 3. 节点 id 在新树存在但旧树不存在 → 属于新增
 * 4. 节点 id 相同但 params 不同 → 属于修改
 *
 * oldTree = TreeNode(id=1, name="create_order", params={})
 * newTree = TreeNode(id=1, name="create_order", params={"retry": 3})
 *
 * 输出： { "added": [ ], "removed": [ ], "modified": [{"id": 1, "old_params": {}, "new_params": {"retry": 3}}] }
 *
 *
 *
 * oldTree结构： 1 (name=A) / \ 2 3 (name=C)
 * newTree结构： 1 (name=A) / \ 4 3 (name=C, params={"mode":2})
 *
 * 输出： { "added": [{"id":4, "name":"B", "params":{}}], "removed": [{"id":2, "name":"B", "params":{}}], "modified": [{"id":3, "old_params":{}, "new_params":{"mode":2}}] }
 *
 *
 *
 * oldTree结构： 10 (name=X) \ 20 (name=Y) \ 30 (name=Z)
 *
 * newTree结构： 10 (name=X) / 40 (name=Y) / 30 (name=Z, params={"threshold":0.8})
 *
 * 输出： { "added": [{"id":40, ...}], "removed": [{"id":20, ...}], "modified": [{"id":30, "old_params":{}, "new_params":{"threshold":0.8}}] }
 *
 * 请使用java言语实现一个函数，找出 newTree 相对于 oldTree 的变更，充分考虑树节点达到亿级后的内存管理问题，
 * 函数只要实现把得到的变更结果存放到一个数据结构就行，不需要打印出来。
 */

/**
 * 定义一个树节点
 */
class TreeNode {
    public int id;
    public String name;
    public Map<String, String> params;
    public TreeNode left;
    public TreeNode right;
}

/**
 * 由于考虑是亿级别的节点数据，
 * 所以考虑使用临时文件本地存储记录对比结果
 */
class ExternalStorageList {
    private final Path storageDir;
    private final String prefix;
    private int fileCounter = 0;

    public ExternalStorageList(String type) {
        this.prefix = type;
        try {
            this.storageDir = Files.createTempDirectory("tree_diff_");
            // 添加shutdown hook自动清理临时文件
            Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void add(Map<String, Object> data) {
        Path file = storageDir.resolve(prefix + "_" + (fileCounter++) + ".tmp");
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(file)))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Iterable<Map<String, Object>> items() {
        return () -> new Iterator<Map<String, Object>>() {
            private int current = 0;
            private ObjectInputStream ois;

            public boolean hasNext() {
                Path file = storageDir.resolve(prefix + "_" + current + ".tmp");
                return Files.exists(file);
            }

            public Map<String, Object> next() {
                try {
                    Path file = storageDir.resolve(prefix + "_" + (current++) + ".tmp");
                    ois = new ObjectInputStream(
                            new BufferedInputStream(Files.newInputStream(file)));
                    return (Map<String, Object>) ois.readObject();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private void cleanup() {
        try {
            Files.walk(storageDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try { Files.deleteIfExists(path); }
                        catch (IOException ignored) {}
                    });
        } catch (IOException ignored) {}
    }
}

/**
 * 对比结果
 */
class TreeDiffResult {
    final ExternalStorageList added = new ExternalStorageList("added");
    final ExternalStorageList deleted = new ExternalStorageList("deleted");
    final ExternalStorageList modified = new ExternalStorageList("modified");
}

public class TreeComparator {

    // 批次处理10w节点
    private static final int BATCH_SIZE = 100000;
    // 采用WorkStealing线程池提高CPU利用率
    private final ExecutorService executor = Executors.newWorkStealingPool();

    /**
     * 对比2个树节点
     * 1. 通过CompletableFuture实现异步任务编排，使用WorkStealing线程池提高CPU利用率
     * 2. 按10w一组进行分块对比
     */
    private TreeDiffResult compareInBatches(TreeNode oldRoot, TreeNode newRoot) {
        TreeDiffResult result = new TreeDiffResult();
        ConcurrentHashMap<Integer, PositionAwareNode> oldMap = new ConcurrentHashMap<>(BATCH_SIZE);
        ConcurrentHashMap<Integer, PositionAwareNode> newMap = new ConcurrentHashMap<>(BATCH_SIZE);

        // 并行构建节点映射
        CompletableFuture<Void> oldTreeTask = CompletableFuture.runAsync(() ->
                buildNodeMap(oldRoot, oldMap, true), executor);
        CompletableFuture<Void> newTreeTask = CompletableFuture.runAsync(() ->
                buildNodeMap(newRoot, newMap, false), executor);

        CompletableFuture.allOf(oldTreeTask, newTreeTask).join();


        // 处理修改的节点
        detectDifferences(oldMap, newMap, result);
        return result;
    }

    /**
     * 构建树节点map
     * 通过DFS方法遍历树，防止栈溢出
     */
    private void buildNodeMap(TreeNode root, ConcurrentHashMap<Integer, PositionAwareNode> map,  boolean isOldTree) {
        Deque<PositionAwareNode> stack = new ArrayDeque<>();
        if (root != null) stack.push(new PositionAwareNode(root, null, false));

        while (!stack.isEmpty()) {
            PositionAwareNode pan = stack.pop();
            map.put(pan.node.id, pan);

            if (pan.node.right != null) {
                stack.push(new PositionAwareNode(pan.node.right, pan.node, false));
            }
            if (pan.node.left != null) {
                stack.push(new PositionAwareNode(pan.node.left, pan.node, true));
            }

            if (map.size() >= BATCH_SIZE) {
                map = new ConcurrentHashMap<>(BATCH_SIZE);
            }
        }
    }

    /**
     *
     * 节点差异对比
     * 1. 分块对比
     */
    private void detectDifferences(Map<Integer, PositionAwareNode> oldMap,  Map<Integer, PositionAwareNode> newMap,    TreeDiffResult result) {
        // 检测删除节点
        oldMap.keySet().parallelStream()
                .filter(id -> !newMap.containsKey(id))
                .forEach(id -> result.deleted.add(createNodeInfo(oldMap.get(id))));

        // 检测新增节点
        newMap.keySet().parallelStream()
                .filter(id -> !oldMap.containsKey(id))
                .forEach(id -> result.added.add(createNodeInfo(newMap.get(id))));

        // 检测修改节点
        newMap.keySet().parallelStream()
                .filter(oldMap::containsKey)
                .forEach(id -> {
                    PositionAwareNode oldNode = oldMap.get(id);
                    PositionAwareNode newNode = newMap.get(id);
                    if (!oldNode.node.params.equals(newNode.node.params) ||
                            !isSamePosition(oldNode, newNode)) {
                        result.modified.add(createModificationInfo(oldNode, newNode));
                    }
                });
    }

    private Map<String, Object> createNodeInfo(PositionAwareNode pan) {
        return ImmutableMap.of("id", pan.node.id, "name", pan.node.name, "params", pan.node.params);
    }

    private Map<String, Object> createModificationInfo(PositionAwareNode oldNode,
                                                       PositionAwareNode newNode) {
        return ImmutableMap.of(
                "id", oldNode.node.id,
                "old_params", oldNode.node.params,
                "new_params", newNode.node.params,
                "position_changed", !isSamePosition(oldNode, newNode)
        );
    }

    private boolean isSamePosition(PositionAwareNode n1, PositionAwareNode n2) {
        return (n1.isLeft == n2.isLeft) &&
                (n1.parent == n2.parent ||
                        (n1.parent != null && n2.parent != null &&
                                n1.parent.id == n2.parent.id));
    }

    /**
     * 节点对比辅助类
     */
    private static class PositionAwareNode {
        final TreeNode node;
        final TreeNode parent;
        final boolean isLeft;

        PositionAwareNode(TreeNode node, TreeNode parent, boolean isLeft) {
            this.node = node;
            this.parent = parent;
            this.isLeft = isLeft;
        }
    }

}