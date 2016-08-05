package com.worth.ifs.invite.resource;

import com.worth.ifs.invite.constant.InviteStatusConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionAssessorInviteResource extends InviteResource {

    private String competitionName;

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }
}
