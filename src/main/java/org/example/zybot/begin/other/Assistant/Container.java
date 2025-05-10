package org.example.zybot.begin.other.Assistant;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//容器类 用来存储已经实现的功能的名称
@Component
public class Container {
    private final Set<String> fieldSet = ConcurrentHashMap.newKeySet();

    public void addField(String field) {
        fieldSet.add(field);
    }

    //若field存在，则返回true
    public boolean containsField(String field) {
        return fieldSet.contains(field);
    }

    public void removeField(String field) {
        fieldSet.remove(field);
    }
    public Set<String> getFieldSet() {
        return fieldSet;
    }
}
