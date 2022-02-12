package org.fenglin;


public class GameInfo {
    private long id = 0;
    private String name = "空";
    private Boolean isUndercover = false;
    private Boolean isAdmin = false;
    private String words = "空";
    private String description = "空";
    private Boolean isVoted = true;
    private int poll = 0;

    public Boolean getIsVoted() {
        return isVoted;
    }

    public void setIsVoted(Boolean isVoted) {
        this.isVoted = isVoted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsUndercover() {
        return isUndercover;
    }

    public void setIsUndercover(Boolean isUndercover) {
        this.isUndercover = isUndercover;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoll() {
        return poll;
    }

    public void setPoll(int poll) {
        this.poll = poll;
    }
}
