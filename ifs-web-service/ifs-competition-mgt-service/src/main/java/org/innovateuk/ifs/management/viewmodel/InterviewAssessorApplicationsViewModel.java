package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Set;

public class InterviewAssessorApplicationsViewModel {

    private final long competitionId;
    private final String competitionName;
    private final UserResource user;
    private final ProfileResource profile;
    private final List<InnovationSectorViewModel> innovationSectors;

    public InterviewAssessorApplicationsViewModel(long competitionId,
                                                  String competitionName,
                                                  UserResource user,
                                                  ProfileResource profile,
                                                  List<InnovationSectorViewModel> innovationSectors
    ) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.user = user;
        this.profile = profile;
        this.innovationSectors = innovationSectors;
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

    public List<InnovationSectorViewModel> getInnovationSectors() {
        return innovationSectors;
    }
}
