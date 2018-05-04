package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Set;

public class InterviewAssessorApplicationsViewModel {

    private final long competitionId;
    private final String competitionName;
    private final UserResource user;
    private final ProfileResource profile;
    private final Set<String> innovationAreas;
    private final List<InterviewAllocatedApplicationRowViewModel> rows;
    private final PaginationViewModel pagination;
    private final long unallocatedApplications;
    private final long allocatedApplications;

    public InterviewAssessorApplicationsViewModel(long competitionId,
                                                  String competitionName,
                                                  UserResource user,
                                                  ProfileResource profile,
                                                  Set<String> innovationAreas,
                                                  List<InterviewAllocatedApplicationRowViewModel> rows,
                                                  PaginationViewModel pagination,
                                                  long unallocatedApplications,
                                                  long allocatedApplications) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.user = user;
        this.profile = profile;
        this.innovationAreas = innovationAreas;
        this.rows = rows;
        this.pagination = pagination;
        this.unallocatedApplications = unallocatedApplications;
        this.allocatedApplications = allocatedApplications;
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

    public List<InterviewAllocatedApplicationRowViewModel> getRows() {
        return rows;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }

    public long getUnallocatedApplications() {
        return unallocatedApplications;
    }

    public long getAllocatedApplications() {
        return allocatedApplications;
    }
}
