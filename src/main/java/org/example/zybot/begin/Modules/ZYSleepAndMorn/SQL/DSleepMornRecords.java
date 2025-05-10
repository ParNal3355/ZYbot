package org.example.zybot.begin.Modules.ZYSleepAndMorn.SQL;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SleepMornRecords")
public class DSleepMornRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String Uid;

    private LocalDateTime time;

    private String Bid;

    private Integer k;

    // Constructors, getters and setters
    public DSleepMornRecords() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getBid() {
        return Bid;
    }

    public void setBid(String bid) {
        Bid = bid;
    }

    public Integer getK() {
        return k;
    }

    public void setK(Integer k) {
        this.k = k;
    }
}
