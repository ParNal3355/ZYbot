package org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Permission;

import jakarta.persistence.*;

//DPermission表的实体类
@Entity
@Table(name = "Permission")
public class DPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 指定主键生成策略为自增
    private Long id;

    @Column(name = "key")
    private String key;

    @Column(name = "fun")
    private String fun;


    public DPermission() {
    }

    public DPermission( String key, String fun) {
        this.key = key;
        this.fun = fun;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFun() {
        return fun;
    }

    public void setFun(String fun) {
        this.fun = fun;
    }
}
