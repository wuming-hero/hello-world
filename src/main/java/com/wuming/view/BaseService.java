package com.wuming.view;

/**
 * 设计一个方式，使得开发者在应用层接入时必须实现相关功能，包含请求体校验，业务异常处理，正常业务处理，日志摘要打印，相关功能可以通过打印日志方式实现，不需要真实实现
 *
 * @author che
 * Created on 2025/6/8 08:45
 */

public abstract class BaseService<Req, Resp> {

    // 必须实现的抽象方法
    protected abstract void validateRequest(Req request);

    protected abstract Resp processBusiness(Req request) throws BusinessException;

    protected abstract String generateLogSummary(Req request, Resp response);

    // 模板方法定义处理流程
    public final Resp execute(Req request) {
        try {
            // 1.请求校验
            validateRequest(request);
            System.out.println("[INFO] 请求校验通过");

            // 2.业务处理
            Resp response = processBusiness(request);
            System.out.println("[INFO] 业务处理完成");

            // 3.日志摘要
            String log = generateLogSummary(request, response);
            System.out.println("[LOG] " + log);

            return response;
        } catch (ValidationException e) {
            System.out.println("[ERROR] 参数校验失败: " + e.getMessage());
            throw e;
        } catch (BusinessException e) {
            System.out.println("[ERROR] 业务异常: " + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            System.out.println("[ERROR] 系统异常: " + e.getClass().getName());
            throw new SystemException("SYSTEM_ERROR");
        }
    }
}

// 自定义异常类
class BusinessException extends RuntimeException {
    private String errorCode;
    // 构造方法省略...


    public String getErrorCode() {
        return errorCode;
    }
}

class ValidationException extends RuntimeException {
}

class SystemException extends RuntimeException {
    private String errorCode;


    public SystemException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}

