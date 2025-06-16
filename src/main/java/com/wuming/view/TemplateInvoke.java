package com.wuming.view;


import java.util.function.Consumer;

/**
 * 设计一个方式，使得开发者在应用层接入时必须实现相关功能，包含请求体校验，业务异常处理，正常业务处理，日志摘要打印，相关功能可以通过打印日志方式实现，不需要真实实现
 *
 * @author che
 * Created on 2025/6/6 16:30
 */
public class TemplateInvoke<T> {

    public static void main(String[] args) {


        // 定义入参处理逻辑
        Consumer<Req> checkProcessor = req -> {

            System.out.println("处理输入: " + req);
        };

        // 定义逻辑处理
        Consumer<Req> processor = req -> {
            System.out.println("执行逻辑处理: " + req);
        };

        // 定义异常处理
        Consumer<Exception> errorProcessor = ex -> {
            System.err.println("发生异常: " + ex.getMessage());
        };

        Req req = new Req();
        // 组合处理逻辑
        process(req, checkProcessor, processor, errorProcessor);

    }


    /**
     * 定义模板方法
     * <p>
     * 优点：
     * <p>
     * 缺点：
     *
     * @param req
     * @param inputProcessor
     * @param logicProcessor
     * @param exceptionHandler
     */
    public static void process(Req req, Consumer<Req> inputProcessor, Consumer<Req> logicProcessor, Consumer<Exception> exceptionHandler) {
        try {
            // 校验参数
            inputProcessor.accept(req);
            // 处理业务逻辑
            logicProcessor.accept(req);
        } catch (Exception ex) {
            // 异常处理
            exceptionHandler.accept(ex);
        }
    }


}

class Req {
    private Long id;
    private String name;

}
