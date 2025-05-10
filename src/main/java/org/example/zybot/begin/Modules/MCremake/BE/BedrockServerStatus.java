package org.example.zybot.begin.Modules.MCremake.BE;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BedrockServerStatus {

    private boolean online;
    private Version version;
    private Players players;
    private Motd motd;

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

    public static class Version {
        @JsonProperty("name")
        private String name;

        public String getName() {
            return name;
        }
    }

    public static class Players {
        @JsonProperty("online")
        private int online;
        @JsonProperty("max")
        private int max;

        public int getOnline() {
            return online;
        }

        public int getMax() {
            return max;
        }
    }

    public static class Motd {
        @JsonProperty("clean")
        private String clean;

        public String getClean() {
            return clean;
        }
    }
}
