package com.androcoders.bingo;

public class Player {
    private String playername, playerid;
    private long bingo;
    private long score;

    public Player(String playername, String playerid, long bingo, long score) {
        this.playername = playername;
        this.playerid = playerid;
        this.bingo = bingo;
        this.score = score;
    }


    public long getBingo() {
        return bingo;
    }

    public void setBingo(long bingo) {
        this.bingo = bingo;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

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
