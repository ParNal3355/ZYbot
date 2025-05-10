package org.example.zybot.begin.Modules.ZYSleepAndMorn.SQL;


import jakarta.persistence.*;

@Entity
@Table(name = "SleepMornStatistics")
public class DSleepMornStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自动生成 ID
    private Integer id;

    @Column(name = "Uid")
    private String uid;

    @Column(name = "Bid")
    private String bid;

    @Column(name = "Num")
    private Integer num;

    @Column(name = "Fuc1")
    private Integer fuc1;

    @Column(name = "Fuc2")
    private Integer fuc2;

    @Column(name = "Fuc3")
    private Integer fuc3;

    // get与set方法
    public DSleepMornStatistics() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getFuc1() {
        return fuc1;
    }

    public void setFuc1(Integer fuc1) {
        this.fuc1 = fuc1;
    }

    public Integer getFuc2() {
        return fuc2;
    }

    public void setFuc2(Integer fuc2) {
        this.fuc2 = fuc2;
    }

    public Integer getFuc3() {
        return fuc3;
    }

    public void setFuc3(Integer fuc3) {
        this.fuc3 = fuc3;
    }
}