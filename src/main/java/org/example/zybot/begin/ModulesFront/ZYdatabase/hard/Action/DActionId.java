package org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;

//Action表的复合主键类
@Embeddable
public class DActionId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(length = 255)
    private String id;

    @Column(name = "room")
    private String room;

    //构造器
    public DActionId(String id, String room) {
        this.id = id;
        this.room = room;
    }
    public DActionId() {
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    // equals() 和 hashCode() 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DActionId that = (DActionId) o;
        return id != null ? id.equals(that.id) && room != null ? room.equals(that.room) : that.room == null : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (room != null ? room.hashCode() : 0);
        return result;
    }
}