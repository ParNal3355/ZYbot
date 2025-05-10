package org.example.zybot.begin.other.strategy;

import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.message.RPerson;

//EventStrategy接口，每个模块的启动部分必须实现该类
public interface EventStrategy {
    RPerson process(Person person);
}