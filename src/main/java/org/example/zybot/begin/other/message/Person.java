package org.example.zybot.begin.other.message;

import java.util.List;

//Person类  作为传输数据的类用来向各模块传输数据
public class Person {
    private String[] message;//切割好的信息内容
    private String Bid;//频道/群聊id
    private String Sid;//房间id
    private String Uid;//用户id
    private String name;//频道中用户的自定义昵称
    private List<String> identity;//用户是否具有任一管理员权限

    // 默认构造器
    public Person() {
    }

    // 带参数的构造器---将所有内容写入 适用于频道事件
    public Person(String[] message, String Bid, String Sid, String Uid, String name) {
        this.message = message;
        this.Bid = Bid;
        this.Sid = Sid;
        this.Uid = Uid;
        this.name = name;
    }

    //带参数的构造器---将除name外的所有内容写入 适用于群聊事件
    public Person(String[] message, String Bid, String Sid, String Uid) {
        this.message = message;
        this.Bid = Bid;
        this.Sid = Sid;
        this.Uid = Uid;
    }

    //getter方法实现
    // message数组的getter方法
    public String[] getMessage() {
        return message != null ? message : new String[]{"-1"};
    }

    // Gid的getter方法
    public String getBid() {
        return Bid != null ? Bid : "0";
    }

    // Sid的getter方法
    public String getSid() {
        return Sid != null ? Sid : "0";
    }

    // Uid的getter方法
    public String getUid() {
        return Uid != null ? Uid : "0";
    }

    // name的getter方法
    public String getName() {
        return name != null ? name : "0";
    }

    // Setter实现
    public void setMessage(String[] message) {
        this.message = message;
    }

    public void setBid(String Gid) {
        this.Bid = Gid;
    }

    public void setSid(String Sid) {
        this.Sid = Sid;
    }

    public void setUid(String Uid) {
        this.Uid = Uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIdentity() {
        return identity;
    }

    public void setIdentity(List<String> identity) {
        this.identity = identity;
    }

}
