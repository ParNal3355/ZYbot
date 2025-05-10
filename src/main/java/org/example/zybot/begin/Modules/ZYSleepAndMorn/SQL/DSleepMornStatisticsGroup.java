package org.example.zybot.begin.Modules.ZYSleepAndMorn.SQL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SleepMornStatisticsGroup")
public class DSleepMornStatisticsGroup {

    @Id
    private String bid;

    @Column(name = "gnum")
    private Integer gnum;

    @Column(name = "snum")
    private Integer snum;

    // Constructors, getters and setters
    public DSleepMornStatisticsGroup() {
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public Integer getGnum() {
        return gnum;
    }

    public void setGnum(Integer gnum) {
        this.gnum = gnum;
    }

    public Integer getSnum() {
        return snum;
    }

    public void setSnum(Integer snum) {
        this.snum = snum;
    }
}