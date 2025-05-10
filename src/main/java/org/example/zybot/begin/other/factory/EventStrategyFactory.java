package org.example.zybot.begin.other.factory;

import org.example.zybot.begin.other.strategy.EventStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

//策略工厂类 自动扫描所有策略类，并根据注解建立策略类与事件类型的关联
@Component
public class EventStrategyFactory {
    private final Map<EventType, List<EventStrategy>> strategyMap = new HashMap<>();

    //初始化strategyMap映射关系
    @Autowired
    public EventStrategyFactory(List<EventStrategy> strategies, ApplicationContext context) {
        for (EventStrategy strategy : strategies) {
            //获取当前策略类的SupportedEvents注解
            SupportedEvents annotations = strategy.getClass().getAnnotation(SupportedEvents.class);
            if (annotations != null) {//不为空，创建映射关系
                for (EventType eventType : annotations.value()) {
                    strategyMap.computeIfAbsent(eventType, k -> new ArrayList<>()).add(strategy);
                }
            }
        }
    }

    //用于根据事件类型检索对应的策略列表
    public List<EventStrategy> getStrategies(EventType eventType) {
        return strategyMap.getOrDefault(eventType, Collections.emptyList());
    }
}