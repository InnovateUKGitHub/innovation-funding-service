package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompletedPercentageResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.transactional.ApplicationSummarisationService;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.fundingdecision.mapper.FundingDecisionMapper;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(config = GlobalMapperConfig.class)
public abstract class ApplicationSummaryMapper {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSummarisationService applicationSummarisationService;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private FundingDecisionMapper fundingDecisionMapper;

    public ApplicationSummaryResource mapToResource(Application source) {

        ApplicationSummaryResource result = new ApplicationSummaryResource();

        ServiceResult<CompletedPercentageResource> percentageResult = applicationService.getProgressPercentageByApplicationId(source.getId());
        if (percentageResult.isSuccess()) {
            result.setCompletedPercentage(percentageResult.getSuccess().getCompletedPercentage().intValue());
        }

        result.setStatus(status(source, result.getCompletedPercentage()));
        result.setId(source.getId());
        result.setName(source.getName());
        result.setDuration(source.getDurationInMonths());
        result.setManageFundingEmailDate(source.getManageFundingEmailDate());
        result.setIneligibleInformed(source.getApplicationProcess().getProcessState() == ApplicationState.INELIGIBLE_INFORMED);
        if (source.getLeadApplicant() != null) {
            result.setLeadApplicant(source.getLeadApplicant().getName());
        }

        ProcessRole leadProcessRole = source.getLeadApplicantProcessRole();
        Optional<Organisation> leadOrganisation = organisationRepository.findById(leadProcessRole.getOrganisationId());
        if (leadOrganisation.isPresent()) {
            result.setLead(leadOrganisation.get().getName());
        }

        if (source.getFundingDecision() != null) {
            result.setFundingDecision(fundingDecisionMapper.mapToResource(source.getFundingDecision()));
        }
        if (source.getApplicationProcess().getProcessState() == ApplicationState.APPROVED) {
            result.setFundingDecision(FundingDecision.FUNDED);
        }

        BigDecimal grantRequested = getGrantRequested(source);
        result.setGrantRequested(grantRequested);

        int numberOfPartners = source.getProcessRoles().stream()
                .filter(processRole -> processRole.getRole().isCollaborator() && processRole.getOrganisationId() != null)
                .collect(Collectors.groupingBy(ProcessRole::getOrganisationId))
                .size();

        result.setNumberOfPartners(numberOfPartners);

        BigDecimal totalProjectCost = getTotalProjectCost(source);
        result.setTotalProjectCost(totalProjectCost);

        result.setInnovationArea(source.getInnovationArea() != null ? source.getInnovationArea().getName() : null);

        result.setInAssessmentPanel(source.isInAssessmentReviewPanel());

        return result;
    }

    private String status(Application source, Integer completedPercentage) {

        if (source.getApplicationProcess().getProcessState() == ApplicationState.SUBMITTED
                || source.getApplicationProcess().getProcessState() == ApplicationState.APPROVED
                || source.getApplicationProcess().getProcessState() == ApplicationState.REJECTED) {
            return "Submitted";
        }

        if (completedPercentage != null && completedPercentage > 50) {
            return "In Progress";
        }
        return "Started";
    }

    private BigDecimal getTotalProjectCost(Application source) {
        ServiceResult<BigDecimal> totalCostResult = applicationSummarisationService.getTotalProjectCost(source);
        if (totalCostResult.isFailure()) {
            return BigDecimal.ZERO;
        }
        return totalCostResult.getSuccess();
    }

    private BigDecimal getGrantRequested(Application source) {
        ServiceResult<BigDecimal> fundingSoughtResult = applicationSummarisationService.getFundingSought(source);
        if (fundingSoughtResult.isFailure()) {
            return BigDecimal.ZERO;
        }
        return fundingSoughtResult.getSuccess();
    }

    public Iterable<ApplicationSummaryResource> mapToResource(Iterable<Application> source) {
        ArrayList<ApplicationSummaryResource> result = new ArrayList<>();
        for (Application application : source) {
            result.add(mapToResource(application));
        }
        return result;
    }
}
