package com.androcoders.bingo;

public class Player {
    private String playername, playerid;

    public Player(String playername, String playerid) {
        this.playername = playername;
        this.playerid = playerid;

    }

    public String getPlayername() {
        return playername;
    }

    public String getPlayerid() {
        return playerid;
    }
}
