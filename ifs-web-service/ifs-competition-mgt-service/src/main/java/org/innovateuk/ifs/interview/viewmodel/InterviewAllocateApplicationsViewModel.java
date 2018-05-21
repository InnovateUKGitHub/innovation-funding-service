package org.innovateuk.ifs.interview.viewmodel;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

/**
 * Holder of model attributes for allocating applications to an assessor
 */
public class InterviewAllocateApplicationsViewModel {
    private final long competitionId;
    private final String competitionName;
    private final UserResource user;
    private final String content;
    private final List<InterviewApplicationResource> interviewApplications;

    public InterviewAllocateApplicationsViewModel(long competitionId,
                                                  String competitionName,
                                                  UserResource user,
                                                  String content,
                                                  List<InterviewApplicationResource> interviewApplications) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.user = user;
        this.content = content;
        this.interviewApplications = interviewApplications;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public UserResource getUser() {
        return user;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getContent() { return content; }

    public List<InterviewApplicationResource> getApplications() {
        return interviewApplications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAllocateApplicationsViewModel that = (InterviewAllocateApplicationsViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(user, that.user)
                .append(content, that.content)
                .append(interviewApplications, that.interviewApplications)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(user)
                .append(content)
                .append(interviewApplications)
                .toHashCode();
    }
}