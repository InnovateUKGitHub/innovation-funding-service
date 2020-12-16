package org.innovateuk.ifs.application.readonly.viewmodel;

public class SupporterAssignmentReadOnlyViewModel {
    private String state;
    private String comments;
    private String userSimpleOrganisation;

    public SupporterAssignmentReadOnlyViewModel(String state, String comments, String userSimpleOrganisation) {
        this.state = state;
        this.comments = comments;
        this.userSimpleOrganisation = userSimpleOrganisation;
    }

    public String getState() {
        return state;
    }

    public String getComments() {
        return comments;
    }

    public String getUserSimpleOrganisation() {
        return userSimpleOrganisation;
    }
}
