package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.string.resource.StringResource;
import org.springframework.stereotype.Service;

/**
 * Rest Service for dealing with Project finance operations
 */
@Service
public class FinanceCheckRestServiceImpl extends BaseRestService implements FinanceCheckRestService {

    @Override
    public RestResult<FinanceCheckResource> getByProjectAndOrganisation(Long projectId, Long organisationId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.ORGANISATION_PATH + "/" + organisationId + FinanceCheckURIs.PATH;
        return getWithRestResult(url, FinanceCheckResource.class);
    }

    @Override
    public RestResult<Void> update(FinanceCheckResource financeCheckResource) {
        String url = FinanceCheckURIs.BASE_URL + FinanceCheckURIs.PATH;
        return postWithRestResult(url, financeCheckResource, Void.class);
    }

    @Override
    public RestResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.PATH;
        return getWithRestResult(url, FinanceCheckSummaryResource.class);
    }

    @Override
    public RestResult<FinanceCheckOverviewResource> getFinanceCheckOverview(Long projectId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.PATH + "/overview";
        return getWithRestResult(url, FinanceCheckOverviewResource.class);
    }

    @Override
    public RestResult<Void> approveFinanceCheck(Long projectId, Long organisationId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.ORGANISATION_PATH + "/" + organisationId + FinanceCheckURIs.PATH + "/approve";
        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(Long projectId, Long organisationId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.ORGANISATION_PATH + "/" + organisationId + FinanceCheckURIs.PATH + "/eligibility";
        return getWithRestResult(url, FinanceCheckEligibilityResource.class);
    }

    @Override
    public RestResult<EligibilityResource> getEligibility(Long projectId, Long organisationId) {
        return getWithRestResult(FinanceCheckURIs.BASE_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/eligibility", EligibilityResource.class);
    }

    @Override
    public RestResult<Void> saveEligibility(Long projectId, Long organisationId, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus) {
        String postUrl = FinanceCheckURIs.BASE_URL + "/" + projectId + "/partner-organisation/" + organisationId +
                "/eligibility/" + eligibility.name() + "/" + eligibilityRagStatus.name();

        return postWithRestResult(postUrl, Void.class);
    }

    @Override
    public RestResult<Void> saveEligibility(Long projectId, Long organisationId, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus, String reason) {

        String postUrl = FinanceCheckURIs.BASE_URL + "/" + projectId + "/partner-organisation/" + organisationId +
                "/eligibility/" + eligibility.name() + "/" + eligibilityRagStatus.name();

        StringResource resource = new StringResource(reason);

        return postWithRestResult(postUrl, resource, Void.class);
    }

    @Override
    public RestResult<ViabilityResource> getViability(Long projectId, Long organisationId) {
        return getWithRestResult(FinanceCheckURIs.BASE_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/viability", ViabilityResource.class);
    }

    @Override
    public RestResult<Void> saveViability(Long projectId, Long organisationId, ViabilityState viability, ViabilityRagStatus viabilityRagStatus) {
        String postUrl = FinanceCheckURIs.BASE_URL + "/" + projectId + "/partner-organisation/" + organisationId +
                "/viability/" + viability.name() + "/" + viabilityRagStatus.name();

        return postWithRestResult(postUrl, Void.class);
    }

    @Override
    public RestResult<Void> saveViability(Long projectId, Long organisationId, ViabilityState viability, ViabilityRagStatus viabilityRagStatus, String reason) {

        String postUrl = FinanceCheckURIs.BASE_URL + "/" + projectId + "/partner-organisation/" + organisationId +
                "/viability/" + viability.name() + "/" + viabilityRagStatus.name();

        StringResource resource = new StringResource(reason);

        return postWithRestResult(postUrl, resource, Void.class);
    }

    @Override
    public RestResult<Void> approvePaymentMilestoneState(Long projectId, Long organisationId) {
        String postUrl = FinanceCheckURIs.BASE_URL + "/" + projectId + "/partner-organisation/" + organisationId +
                "/milestones/approve";

        return postWithRestResult(postUrl, Void.class);
    }

    @Override
    public RestResult<Void> resetPaymentMilestoneState(Long projectId, Long organisationId, String retractionReason) {
        String postUrl = FinanceCheckURIs.BASE_URL + "/" + projectId + "/partner-organisation/" + organisationId +
                "/milestones/reset";
        StringResource content = new StringResource(retractionReason);

        return postWithRestResult(postUrl, content, Void.class);
    }

    @Override
    public RestResult<PaymentMilestoneResource> getPaymentMilestoneState(Long projectId, Long organisationId) {
        return getWithRestResult(FinanceCheckURIs.BASE_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/milestones/state", PaymentMilestoneResource.class);
    }
}
