package org.example.zybot.begin.Modules.MCremake.JE;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class JavaServerStatus {
    private boolean online; // 服务器在线状态
    private Version version; // 服务器版本嵌套类
    private Players players; // 玩家信息嵌套类
    private Motd motd; // 服务器标语嵌套类

    // 嵌套的Version类
    public static class Version {
        @JsonProperty("name_raw")
        private String nameRaw;
        @JsonProperty("name_clean")
        private String nameClean;

        public String getNameClean() {// 服务器版本
            return nameClean;
        }
    }

    // 嵌套的Players类
    public static class Players {
        private int online; // 当前在线玩家
        private int max; // 服务器最大玩家
        private List<JsonNode> list; // 玩家列表

        public int getOnline() {// 服务器在线玩家
            return online;
        }
        public int getMax() {// 服务器最大玩家
            return max;
        }
        public List<JsonNode> getList() {// 玩家列表
            return list;
        }

    }

    // 嵌套的Motd类
    public static class Motd {
        @JsonProperty("raw")
        private String raw;
        @JsonProperty("clean")
        private String clean;

        public String getClean() {// 服务器标语
            return clean;
        }
    }


    public boolean isOnline() {
        return online;
    }

    public Version getVersion() {
        return version;
    }

    public Players getPlayers() {
        return players;
    }

    public Motd getMotd() {
        return motd;
    }

}