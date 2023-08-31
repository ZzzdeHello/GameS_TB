package com.zzzde.game.machine.aop.tools;

import cn.hutool.core.map.MapUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * springboot 提供的钩子方法，用于Bean初始化开始和结束时间调用
 *
 * @author zzzde
 * @version 1.0
 * @date 2023/4/27 15:10
 */
@Component
public class BeanCostTimeProcessor implements BeanPostProcessor {
    private Map<String, Long> costMap = MapUtil.newConcurrentHashMap();
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        costMap.put(beanName, System.currentTimeMillis());
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (costMap.containsKey(beanName)) {
            Long start = costMap.get(beanName);
            long cost  = System.currentTimeMillis() - start;
            if (cost > 0) {
                costMap.put(beanName, cost);
                System.out.println("bean: " + beanName + "\ttime: " + cost);
            }
        }
        return bean;
    }
}
