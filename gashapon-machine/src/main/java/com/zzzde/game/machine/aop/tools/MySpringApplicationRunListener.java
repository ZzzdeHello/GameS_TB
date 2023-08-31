package com.zzzde.game.machine.aop.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/4/27 15:46
 */
@Slf4j
public class MySpringApplicationRunListener extends EventPublishingRunListener {

    public MySpringApplicationRunListener(SpringApplication application, String[] args) {
        super(application, args);
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        log.info("====>>====>>====>>starting {}", LocalDateTime.now());
        super.starting(bootstrapContext);
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        log.info("====>>====>>====>>environmentPrepared {}", LocalDateTime.now());
        super.environmentPrepared(bootstrapContext, environment);
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        log.info("====>>====>>====>>contextPrepared {}", LocalDateTime.now());
        super.contextPrepared(context);
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        log.info("====>>====>>====>>contextLoaded {}", LocalDateTime.now());
        super.contextLoaded(context);
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        log.info("====>>====>>====>>started {}", LocalDateTime.now());
        super.started(context, timeTaken);
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        log.info("====>>====>>====>>ready {}", LocalDateTime.now());
        super.ready(context, timeTaken);
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        log.info("====>>====>>====>>failed {}", LocalDateTime.now());
        super.failed(context, exception);
    }
}
