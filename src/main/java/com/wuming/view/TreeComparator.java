//package com.wuming.view;
//
///**
// * @author che
// * Created on 2025/6/4 14:29
// */
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.UncheckedIOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.ArrayDeque;
//import java.util.Comparator;
//import java.util.Deque;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * 定义一个树节点
// */
//class TreeNode {
//    public int id;
//    public String name;
//    public Map<String, String> params;
//    public TreeNode left;
//    public TreeNode right;
//}
//
///**
// * 由于考虑是亿级别的节点数据，
// * 所以考虑使用临时文件本地存储记录对比结果
// */
//class ExternalStorageList {
//    private final Path storageDir;
//    private final String prefix;
//    private int fileCounter = 0;
//
//    public ExternalStorageList(String type) {
//        this.prefix = type;
//        try {
//            this.storageDir = Files.createTempDirectory("tree_diff_");
//            // 添加shutdown hook自动清理临时文件
//            Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }
//
//    public void add(Map<String, Object> data) {
//        Path file = storageDir.resolve(prefix + "_" + (fileCounter++) + ".tmp");
//        try (ObjectOutputStream oos = new ObjectOutputStream(
//                new BufferedOutputStream(Files.newOutputStream(file)))) {
//            oos.writeObject(data);
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }
//
//    public Iterable<Map<String, Object>> items() {
//        return () -> new Iterator<Map<String, Object>>() {
//            private int current = 0;
//            private ObjectInputStream ois;
//
//            public boolean hasNext() {
//                Path file = storageDir.resolve(prefix + "_" + current + ".tmp");
//                return Files.exists(file);
//            }
//
//            public Map<String, Object> next() {
//                try {
//                    Path file = storageDir.resolve(prefix + "_" + (current++) + ".tmp");
//                    ois = new ObjectInputStream(
//                            new BufferedInputStream(Files.newInputStream(file)));
//                    return (Map<String, Object>) ois.readObject();
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        };
//    }
//
//    private void cleanup() {
//        try {
//            Files.walk(storageDir)
//                    .sorted(Comparator.reverseOrder())
//                    .forEach(path -> {
//                        try { Files.deleteIfExists(path); }
//                        catch (IOException ignored) {}
//                    });
//        } catch (IOException ignored) {}
//    }
//}
//
///**
// * 对比结果
// */
//class TreeDiffResult {
//    final ExternalStorageList added = new ExternalStorageList("added");
//    final ExternalStorageList deleted = new ExternalStorageList("deleted");
//    final ExternalStorageList modified = new ExternalStorageList("modified");
//}
//
//public class TreeComparator {
//
//    // 批次处理10w节点
//    private static final int BATCH_SIZE = 100000;
//    // 采用WorkStealing线程池提高CPU利用率
//    private final ExecutorService executor = Executors.newWorkStealingPool();
//
//    /**
//     * 对比2个树节点
//     * 1. 通过CompletableFuture实现异步任务编排，使用WorkStealing线程池提高CPU利用率
//     * 2. 按10w一组进行分块对比
//     */
//    private TreeDiffResult compareInBatches(TreeNode oldRoot, TreeNode newRoot) {
//        TreeDiffResult result = new TreeDiffResult();
//        ConcurrentHashMap<Integer, PositionAwareNode> oldMap = new ConcurrentHashMap<>(BATCH_SIZE);
//        ConcurrentHashMap<Integer, PositionAwareNode> newMap = new ConcurrentHashMap<>(BATCH_SIZE);
//
//        // 并行构建节点映射
//        CompletableFuture<Void> oldTreeTask = CompletableFuture.runAsync(() ->
//                buildNodeMap(oldRoot, oldMap, true), executor);
//        CompletableFuture<Void> newTreeTask = CompletableFuture.runAsync(() ->
//                buildNodeMap(newRoot, newMap, false), executor);
//
//        CompletableFuture.allOf(oldTreeTask, newTreeTask).join();
//
//
//        // 处理修改的节点
//        detectDifferences(oldMap, newMap, result);
//        return result;
//    }
//
//    /**
//     * 构建树节点map
//     * 通过DFS方法遍历树，防止栈溢出
//     */
//    private void buildNodeMap(TreeNode root, ConcurrentHashMap<Integer, PositionAwareNode> map,  boolean isOldTree) {
//        Deque<PositionAwareNode> stack = new ArrayDeque<>();
//        if (root != null) stack.push(new PositionAwareNode(root, null, false));
//
//        while (!stack.isEmpty()) {
//            PositionAwareNode pan = stack.pop();
//            map.put(pan.node.id, pan);
//
//            if (pan.node.right != null) {
//                stack.push(new PositionAwareNode(pan.node.right, pan.node, false));
//            }
//            if (pan.node.left != null) {
//                stack.push(new PositionAwareNode(pan.node.left, pan.node, true));
//            }
//
//            if (map.size() >= BATCH_SIZE) {
//                map = new ConcurrentHashMap<>(BATCH_SIZE);
//            }
//        }
//    }
//
//    /**
//     *
//     * 节点差异对比
//     * 1. 分块对比
//     */
//    private void detectDifferences(Map<Integer, PositionAwareNode> oldMap,  Map<Integer, PositionAwareNode> newMap,    TreeDiffResult result) {
//        // 检测删除节点
//        oldMap.keySet().parallelStream()
//                .filter(id -> !newMap.containsKey(id))
//                .forEach(id -> result.deleted.add(createNodeInfo(oldMap.get(id))));
//
//        // 检测新增节点
//        newMap.keySet().parallelStream()
//                .filter(id -> !oldMap.containsKey(id))
//                .forEach(id -> result.added.add(createNodeInfo(newMap.get(id))));
//
//        // 检测修改节点
//        newMap.keySet().parallelStream()
//                .filter(oldMap::containsKey)
//                .forEach(id -> {
//                    PositionAwareNode oldNode = oldMap.get(id);
//                    PositionAwareNode newNode = newMap.get(id);
//                    if (!oldNode.node.params.equals(newNode.node.params) ||
//                            !isSamePosition(oldNode, newNode)) {
//                        result.modified.add(createModificationInfo(oldNode, newNode));
//                    }
//                });
//    }
//
//    private Map<String, Object> createNodeInfo(PositionAwareNode pan) {
//        return Map.of("id", pan.node.id, "name", pan.node.name, "params", pan.node.params);
//    }
//
//    private Map<String, Object> createModificationInfo(PositionAwareNode oldNode,
//                                                       PositionAwareNode newNode) {
//        return Map.of(
//                "id", oldNode.node.id,
//                "old_params", oldNode.node.params,
//                "new_params", newNode.node.params,
//                "position_changed", !isSamePosition(oldNode, newNode)
//        );
//    }
//
//    private boolean isSamePosition(PositionAwareNode n1, PositionAwareNode n2) {
//        return (n1.isLeft == n2.isLeft) &&
//                (n1.parent == n2.parent ||
//                        (n1.parent != null && n2.parent != null &&
//                                n1.parent.node.id == n2.parent.node.id));
//    }
//
//    /**
//     * 节点对比辅助类
//     */
//    private static class PositionAwareNode {
//        public TreeNode node;
//        public TreeNode parent;
//        public boolean isLeft;
//
//        PositionAwareNode(TreeNode node, TreeNode parent, boolean isLeft) {
//            this.node = node;
//            this.parent = parent;
//            this.isLeft = isLeft;
//        }
//    }
//
//}
