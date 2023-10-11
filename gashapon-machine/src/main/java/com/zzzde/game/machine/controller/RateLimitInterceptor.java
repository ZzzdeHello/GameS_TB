package com.zzzde.game.machine.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.zzzde.game.tb.common.ResponseEnum;
import com.zzzde.game.tb.common.dal.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author zzzde
 * @Date 2023/10/10
 */

@Slf4j
public class RateLimitInterceptor implements AsyncHandlerInterceptor {

    /**
     * 设置QPS 每秒可访问数量
     */
    private static final RateLimiter rateLimiter = RateLimiter.create(10);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ResponseEnum responseEnum;
        try {
            responseEnum = preRateLimitFilter(request);
            if (responseEnum.equals(ResponseEnum.OK)) {
                return true;
            }
        } catch (Exception e) {
            log.error("Interceptor Error Exception ", e);
            responseEnum = ResponseEnum.FAIL;
        }
        handlerErrorResponse(responseEnum, response);
        return false;
    }

    /**
     * 限流器过滤
     *
     * @param request
     * @return
     */
    public ResponseEnum preRateLimitFilter(HttpServletRequest request) {
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

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        AsyncHandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AsyncHandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
