package org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action;


import jakarta.persistence.*;
import java.time.LocalDateTime;

//Action表的实体类
@Entity
@Table(name = "Action")
public class DAction {

    @EmbeddedId
    private DActionId id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "num")
    private Integer num;

    @Column(name = "act")
    private Integer act;

    @Column(name = "bata1")
    private String bata1;

    @Column(name = "bata2")
    private String bata2;

    @Column(name = "bata3")
    private String bata3;

    @Column(name = "bata4")
    private String bata4;

    @Column(name = "bata5")
    private String bata5;

    @Column(name = "CTime")
    private LocalDateTime CTime;

    // Getters and setters for DActionId
    public DActionId getId() {
        return id;
    }

    public void setId(DActionId id) {
        this.id = id;
    }


    // Other getters and setters
    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getAct() {
        return act;
    }

    public void setAct(Integer act) {
        this.act = act;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBata1() {
        return bata1;
    }

    public void setBata1(String bata1) {
        this.bata1 = bata1;
    }

    public String getBata2() {
        return bata2;
    }

    public void setBata2(String bata2) {
        this.bata2 = bata2;
    }

    public String getBata3() {
        return bata3;
    }

    public void setBata3(String bata3) {
        this.bata3 = bata3;
    }

    public String getBata4() {
        return bata4;
    }

    public void setBata4(String bata4) {
        this.bata4 = bata4;
    }

    public String getBata5() {
        return bata5;
    }

    public void setBata5(String bata5) {
        this.bata5 = bata5;
    }

    public LocalDateTime getCreateTime() {
        return CTime;
    }

    // 在实体被持久化之前，自动设置创建时间
    @PrePersist
    protected void onCreate() {
        CTime = LocalDateTime.now();
    }
    // Constructors, hashCode, equals, toString as needed
}