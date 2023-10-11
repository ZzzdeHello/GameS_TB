package com.zzzde.game.machine.aop.aspect;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.zzzde.game.tb.common.ResponseEnum;
import com.zzzde.game.tb.common.dal.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author zzzde
 * @Date 2023/10/11
 */
@Component
@Scope
@Aspect
@Slf4j
public class RateLimitAspectAopImpl {

    @Autowired
    private HttpServletResponse response;

    /**
     * 设置QPS 每秒可访问数量
     */
    private static final RateLimiter rateLimiter = RateLimiter.create(0.1);

    /**
     * 切点注解
     */
    @Pointcut("@annotation(com.zzzde.game.machine.aop.tools.RateLimitAspect)")
    public void serviceLimit() {

    }

    /**
     * 注解方处理
     *
     * @param joinPoint
     * @return
     */
    @Around("serviceLimit()")
    public Object around(ProceedingJoinPoint joinPoint) {
        ResponseEnum limitFilterResult = preRateLimitFilter();
        Object obj = null;
        try {
            if (limitFilterResult.equals(ResponseEnum.OK)) {
                // 若能获取到则，正常处理
                obj = joinPoint.proceed();
            } else {
                handlerErrorResponse(limitFilterResult,response);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 限流器过滤
     *
     * @return
     */
    public ResponseEnum preRateLimitFilter() {
        if (rateLimiter.tryAcquire()) {
            log.info("请求成功");
            return ResponseEnum.OK;
        } else {
            log.warn("请求被限制住了");
            return ResponseEnum.LIMIT;
        }
    }

    /**
     * 返回信息封装
     *
     * @param responseEnum
     * @param response
     */
    private void handlerErrorResponse(ResponseEnum responseEnum, HttpServletResponse response) {
        ResponseDTO<?> dto = new ResponseDTO();
        dto.setCode(responseEnum.getCode());
        dto.setMessage(responseEnum.getMessage());
        try {
            write(dto, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(ResponseDTO<?> dto, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(JSON.toJSONString(dto).getBytes(StandardCharsets.UTF_8));
    }
}
