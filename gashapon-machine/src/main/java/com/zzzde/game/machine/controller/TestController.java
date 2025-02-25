package com.zzzde.game.machine.controller;

import com.zzzde.game.machine.aop.tools.RateLimitAspect;
import com.zzzde.game.tb.common.ResponseEnum;
import com.zzzde.game.tb.common.dal.ResponseDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author zzzde
 * @Date 2023/10/10
 */
@Controller
@RequestMapping("/machine/test")
public class TestController {

    // 使用线程池处理异步任务
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @PostMapping("limit")
    @ResponseBody
    public ResponseDTO<String> testLimit(@RequestParam("test") String test) {
        ResponseDTO<String> dto = new ResponseDTO<>();
        dto.setCode(ResponseEnum.OK.getCode());
        dto.setMessage(ResponseEnum.OK.getMessage());
        dto.setData(test + "请求成功服务器添加的字符串");
        return dto;
    }

    @PostMapping("aspectLimit")
    @ResponseBody
    @RateLimitAspect
    public ResponseDTO<String> testAspectLimit(@RequestParam("test") String test) {
        ResponseDTO<String> dto = new ResponseDTO<>();
        dto.setCode(ResponseEnum.OK.getCode());
        dto.setMessage(ResponseEnum.OK.getMessage());
        dto.setData(test + "请求成功服务器添加的字符串");
        return dto;
    }

    @PostMapping("test_sse")
    @ResponseBody
    @RateLimitAspect
    public SseEmitter testSse(@RequestParam("message") String message) {
        SseEmitter emitter = new SseEmitter(60_000L);  // 设置超时时间为60秒
        executor.submit(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    TimeUnit.SECONDS.sleep(1);  // 模拟延迟
                    emitter.send("实时消息更新 " + i + ":" + message);  // 发送消息
                }
                emitter.complete();  // 完成推送
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);  // 处理异常
            }
        });
        return emitter;
    }
}
