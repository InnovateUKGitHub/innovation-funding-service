package org.innovateuk.ifs.activitylog.advice;

import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;

public interface TestActivityLogService {

    @Activity(type = ActivityType.APPLICATION_SUBMITTED, applicationId = "applicationId")
    ServiceResult<Void> withApplicationId(long applicationId);

    @Activity(type = ActivityType.APPLICATION_SUBMITTED, projectId = "projectId")
    ServiceResult<Void> withProjectId(long projectId);

    @Activity(type = ActivityType.APPLICATION_SUBMITTED, projectOrganisationCompositeId = "projectOrganisationCompositeId")
    ServiceResult<Void> withProjectOrganisationCompositeId(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @Activity(type = ActivityType.APPLICATION_SUBMITTED, applicationId = "notMatching")
    ServiceResult<Void> withNotMatchingApplicationId(long applicationId);

    @Activity(type = ActivityType.APPLICATION_SUBMITTED, projectId = "notMatching")
    ServiceResult<Void> withNotMatchingProjectId(long projectId);

    @Activity(type = ActivityType.APPLICATION_SUBMITTED, projectOrganisationCompositeId = "notMatching")
    ServiceResult<Void> withNotMatchingProjectOrganisationCompositeId(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @Activity(type = ActivityType.APPLICATION_SUBMITTED, applicationId = "applicationId", condition = "condition")
    ServiceResult<Void> withApplicationIdConditional(long applicationId, boolean conditional);

    default boolean condition(long applicationId, boolean conditional) {
        return conditional;
    }

    @Activity(type = ActivityType.APPLICATION_SUBMITTED, applicationId = "applicationId", condition = "notMatching")
    ServiceResult<Void> withApplicationIdNotMatchingConditional(long applicationId, boolean conditional);

    @Activity(type = ActivityType.APPLICATION_SUBMITTED, applicationId = "applicationId")
    ServiceResult<Void> withApplicationIdServiceFailure(long applicationId);

    @Activity(type = ActivityType.APPLICATION_SUBMITTED, applicationId = "applicationId")
    void withApplicationIdNotServiceResult(long applicationId);
}
