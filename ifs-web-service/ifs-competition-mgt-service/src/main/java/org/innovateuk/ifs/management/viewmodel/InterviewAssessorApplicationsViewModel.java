package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Set;

public class InterviewAssessorApplicationsViewModel {

    private final long competitionId;
    private final String competitionName;
    private final UserResource user;
    private final ProfileResource profile;
    private final Set<String> innovationAreas;

    public InterviewAssessorApplicationsViewModel(long competitionId,
                                                  String competitionName,
                                                  UserResource user,
                                                  ProfileResource profile,
                                                  Set<String> innovationAreas
    ) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.user = user;
        this.profile = profile;
        this.innovationAreas = innovationAreas;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public UserResource getUser() {
        return user;
    }

    public ProfileResource getProfile() {
        return profile;
    }

    public Set<String> getInnovationAreas() {
        return innovationAreas;
    }
}
