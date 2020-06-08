package org.innovateuk.ifs.competition.resource;

public enum PostAwardService {

    IFS_POST_AWARD("IFS Post Award"),
    CONNECT("_connect");

    private String displayName;

    PostAwardService(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isIfsPostAward() {
        return this == IFS_POST_AWARD;
    }

    public boolean isConnect() {
        return this == CONNECT;
    }
}
