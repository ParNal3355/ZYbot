package org.example.zybot.begin.ModulesFront.ZYdatabase.hard.MC_BEName;

import jakarta.persistence.*;

@Entity
@Table(name = "MC_BEName", uniqueConstraints = {
        @UniqueConstraint(name = "idx_mc_bename_bid_ip_port", columnNames = {"bid", "IP", "post"}),
        @UniqueConstraint(name = "idx_mc_bename_bid_name", columnNames = {"bid", "name"})
})
public class DMC_BEName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String bid;

    @Column(name = "IP")
    private String ip;

    private Integer post;

    private String name;

    // Constructors
    public DMC_BEName() {
    }

    public DMC_BEName(String bid, String ip, Integer post, String name) {
        this.bid = bid;
        this.ip = ip;
        this.post = post;
        this.name = name;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}