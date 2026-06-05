package com.wuming.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用层接入约定：通过模板方法 {@link #execute(Object)} 固定处理顺序，子类必须实现四类能力——
 * <ul>
 *   <li>请求体校验 — {@link #validateRequest(Object)}</li>
 *   <li>正常业务处理 — {@link #processBusiness(Object)}</li>
 *   <li>业务异常处理 — {@link #handleBusinessException(Object, BusinessException)}（如记日志、打点，通常仍向上抛出）</li>
 *   <li>日志摘要 — {@link #generateLogSummary(Object, Object)}（成功路径打印摘要）</li>
 * </ul>
 * 框架内其它步骤仅通过日志体现，不强制真实基础设施实现。
 *
 * @param <Req>  请求类型
 * @param <Resp> 响应类型
 * @author che
 */
public abstract class BaseService<Req, Resp> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 请求体 / 入参校验；不通过时抛出 {@link ValidationException}。
     */
    protected abstract void validateRequest(Req request);

    /**
     * 正常业务处理；业务规则不满足时抛出 {@link BusinessException}。
     */
    protected abstract Resp processBusiness(Req request);

    /**
     * 业务异常发生时的处理（记录摘要、错误码、可选监控上报等）。
     * 模板方法在调用后会再次抛出该异常，便于统一出口或外层转换为 HTTP 响应。
     */
    protected abstract void handleBusinessException(Req request, BusinessException e);

    /**
     * 成功路径下的日志摘要字符串（关键字段、业务单号等），由 {@link #execute(Object)} 打印为 INFO。
     */
    protected abstract String generateLogSummary(Req request, Resp response);

    /**
     * 固定流程：校验 → 业务 → 成功摘要日志；校验失败 / 业务异常 / 系统异常分支分别记录。
     */
    public final Resp execute(Req request) {
        long startNs = System.nanoTime();
        try {
            validateRequest(request);
            log.debug("[{}] request validation passed", logTag());

            Resp response = processBusiness(request);
            log.debug("[{}] business processing finished", logTag());

            String summary = generateLogSummary(request, response);
            long costMs = (System.nanoTime() - startNs) / 1_000_000L;
            log.info("[SUMMARY][{}] {} | costMs={}", logTag(), summary, costMs);
            return response;

        } catch (ValidationException e) {
            log.warn("[{}] validation failed: {}", logTag(), e.getMessage());
            throw e;
        } catch (BusinessException e) {
            handleBusinessException(request, e);
            throw e;
        } catch (Exception e) {
            log.error("[{}] unexpected system error", logTag(), e);
            throw new SystemException("SYSTEM_ERROR", e);
        }
    }

    /** 日志前缀中的业务标识，子类可覆盖为接口名、场景码等 */
    protected String logTag() {
        return getClass().getSimpleName();
    }
}

/** 业务语义异常，携带对外错误码 */
class BusinessException extends RuntimeException {
    private final String errorCode;

    BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    String getErrorCode() {
        return errorCode;
    }
}

/** 入参 / 请求体不合法 */
class ValidationException extends RuntimeException {
    ValidationException(String message) {
        super(message);
    }
}

/** 未预期系统错误包装 */
class SystemException extends RuntimeException {
    private final String errorCode;

    SystemException(String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    String getErrorCode() {
        return errorCode;
    }
}
