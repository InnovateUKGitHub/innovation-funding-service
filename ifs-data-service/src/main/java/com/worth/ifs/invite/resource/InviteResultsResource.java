package com.worth.ifs.invite.resource;

public class InviteResultsResource {
    private int invitesSendSuccess;
    private int invitesSendFailure;

    public int getInvitesSendSuccess() {
        return invitesSendSuccess;
    }

    public void setInvitesSendSuccess(int invitesSendSuccess) {
        this.invitesSendSuccess = invitesSendSuccess;
    }

    public int getInvitesSendFailure() {
        return invitesSendFailure;
    }

    public void setInvitesSendFailure(int invitesSendFailure) {
        this.invitesSendFailure = invitesSendFailure;
    }
}
