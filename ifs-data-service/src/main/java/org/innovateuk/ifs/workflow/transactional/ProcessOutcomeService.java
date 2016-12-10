package org.innovateuk.ifs.workflow.transactional;

import org.innovateuk.ifs.assessment.resource.AssessmentOutcomes;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.security.access.prepost.PostAuthorize;

public interface ProcessOutcomeService {
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ProcessOutcomeResource> findOne(Long id);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ProcessOutcomeResource> findLatestByProcess(Long processId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ProcessOutcomeResource> findLatestByProcessAndOutcomeType(Long processId, String outcomeType);
}
