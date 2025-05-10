package org.example.zybot.begin.ModulesFront.ZYdatabase.hard.CrazyThursday;


import jakarta.persistence.*;

@Entity
@Table(name = "Crazy_Thursday") // 指定表名
public class DCrazyThursday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 指定主键生成策略为自增
    private Integer id;

    //疯狂星期四文案
    private String text;

    // 构造函数
    public DCrazyThursday() {
    }

    // 带参数的构造函数
    public DCrazyThursday(String text) {
        this.text = text;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}