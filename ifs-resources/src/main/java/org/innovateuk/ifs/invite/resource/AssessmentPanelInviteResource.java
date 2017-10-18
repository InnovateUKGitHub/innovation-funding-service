package org.innovateuk.ifs.invite.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.invite.constant.InviteStatus;

/**
 * Created by ecormack on 16/10/17.
 */
public class AssessmentPanelInviteResource {

    private InviteStatus status;

    private String hash;

    private long competitionId;

    private String competitionName;

    public AssessmentPanelInviteResource(String hash,
                                         long competitionId,
                                         String competitionName,
                                         InviteStatus status) {
        this.hash = hash;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.status = status;
    }

    private AssessmentPanelInviteResource() {
        // no-arg constructor
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public void setStatus(InviteStatus status) {
        this.status = status;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    @JsonIgnore
    public boolean isPending() {
        return status == InviteStatus.SENT;
    }
}
