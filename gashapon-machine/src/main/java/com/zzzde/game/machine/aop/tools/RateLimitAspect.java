package com.zzzde.game.machine.aop.tools;

import java.lang.annotation.*;

/**
 * 基于GUAVA工具包实现的限流注解
 *
 * @Author zzzde
 * @Date 2023/10/11
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimitAspect {
}
