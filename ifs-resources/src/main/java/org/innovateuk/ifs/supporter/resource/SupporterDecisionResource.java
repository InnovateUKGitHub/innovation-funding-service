package org.innovateuk.ifs.supporter.resource;

import java.util.Objects;

public class SupporterDecisionResource {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupporterDecisionResource that = (SupporterDecisionResource) o;
        return accept == that.accept &&
                Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accept, comments);
    }
}
