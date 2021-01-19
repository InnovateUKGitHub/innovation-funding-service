package org.innovateuk.ifs.procurement.milestone.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.procurement.milestone.resource.PaymentMilestoneResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectProcurementMilestoneRestServiceImpl
        extends BaseProcurementMilestoneRestServiceImpl<PaymentMilestoneResource>
        implements ProjectProcurementMilestoneRestService {

    protected ProjectProcurementMilestoneRestServiceImpl() {
        super("/project-procurement-milestone");
    }

    @Override
    public RestResult<List<PaymentMilestoneResource>> getByProjectIdAndOrganisationId(long projectId, long organisationId) {
        return getWithRestResult(getMilestoneUrl() + String.format("/project/%d/organisation/%d", projectId, organisationId),
                new ParameterizedTypeReference<List<PaymentMilestoneResource>>() {});
    }

    @Override
    public RestResult<List<PaymentMilestoneResource>> getByProjectId(long projectId) {
        return getWithRestResult(getMilestoneUrl() + String.format("/project/%d", projectId),
                new ParameterizedTypeReference<List<PaymentMilestoneResource>>() {});
    }

    @Override
    protected Class<PaymentMilestoneResource> getResourceClass() {
        return PaymentMilestoneResource.class;
    }
}
