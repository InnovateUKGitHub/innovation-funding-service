package org.innovateuk.ifs.procurement.milestone.service;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
public class ApplicationProcurementMilestoneRestServiceImpl
        extends BaseProcurementMilestoneRestServiceImpl<ApplicationProcurementMilestoneResource>
        implements ApplicationProcurementMilestoneRestService {

    protected ApplicationProcurementMilestoneRestServiceImpl() {
        super("/application-procurement-milestone");
    }

    @Override
    public RestResult<List<ApplicationProcurementMilestoneResource>> getByApplicationIdAndOrganisationId(long applicationId, long organisationId) {
        return getWithRestResult(getMilestoneUrl() + String.format("/application/%d/organisation/%d", applicationId, organisationId),
                new ParameterizedTypeReference<List<ApplicationProcurementMilestoneResource>>() {});
    }

    @Override
    public RestResult<Optional<Integer>> findMaxByApplicationId(long applicationId) {
        return getWithRestResult(format(getMilestoneUrl() + "/application/%d/max-milestone-month", applicationId), Integer.class).toOptionalIfNotFound();
    }

    @Override
    protected Class<ApplicationProcurementMilestoneResource> getResourceClass() {
        return ApplicationProcurementMilestoneResource.class;
    }
}
