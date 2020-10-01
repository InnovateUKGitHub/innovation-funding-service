package org.innovateuk.ifs.cofunder.resource;

public class CofunderDecisionResource {
    private boolean accept;
    private String comments;

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
