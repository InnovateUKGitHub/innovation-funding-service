package org.innovateuk.ifs.management.assessor.viewmodel;

import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.interview.model.InterviewAllocatedApplicationRowViewModel;
import org.innovateuk.ifs.management.competition.viewmodel.InnovationSectorViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public class InterviewAssessorApplicationsViewModel {

    private final long competitionId;
    private final String competitionName;
    private final UserResource user;
    private final ProfileResource profile;
    private final List<InnovationSectorViewModel> innovationSectors;
    private final List<InterviewAllocatedApplicationRowViewModel> rows;
    private final Pagination pagination;
    private final long unallocatedApplications;
    private final long allocatedApplications;
    private final boolean selectAllDisabled;
    private final String originQuery;

    public InterviewAssessorApplicationsViewModel(long competitionId,
                                                  String competitionName,
                                                  UserResource user,
                                                  ProfileResource profile,
                                                  List<InnovationSectorViewModel> innovationSectors,
                                                  List<InterviewAllocatedApplicationRowViewModel> rows,
                                                  Pagination pagination,
                                                  long unallocatedApplications,
                                                  long allocatedApplications,
                                                  boolean selectAllDisabled,
                                                  String originQuery) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.user = user;
        this.profile = profile;
        this.innovationSectors = innovationSectors;
        this.rows = rows;
        this.pagination = pagination;
        this.unallocatedApplications = unallocatedApplications;
        this.allocatedApplications = allocatedApplications;
        this.selectAllDisabled = selectAllDisabled;
        this.originQuery = originQuery;
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

    public List<InterviewAllocatedApplicationRowViewModel> getRows() {
        return rows;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public long getUnallocatedApplications() {
        return unallocatedApplications;
    }

    public long getAllocatedApplications() {
        return allocatedApplications;
    }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public String getOriginQuery() {
        return originQuery;
    }
}
