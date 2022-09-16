package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.transactional.ApplicationSummarisationService;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.fundingdecision.mapper.DecisionMapper;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
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
    private DecisionMapper decisionMapper;

    public ApplicationSummaryResource mapToResource(Application source) {

        ApplicationSummaryResource result = new ApplicationSummaryResource();

        result.setCompletedPercentage(source.getCompletion().intValue());
        result.setStatus(status(source, result.getCompletedPercentage()));
        result.setId(source.getId());
        result.setName(source.getName());
        result.setDuration(source.getDurationInMonths());
        result.setManageDecisionEmailDate(source.getManageDecisionEmailDate());
        result.setIneligibleInformed(source.getApplicationProcess().getProcessState() == ApplicationState.INELIGIBLE_INFORMED);
        if (source.getLeadApplicant() != null) {
            result.setLeadApplicant(source.getLeadApplicant().getName());
        }

        ProcessRole leadProcessRole = source.getLeadApplicantProcessRole();
        Optional<Organisation> leadOrganisation = organisationRepository.findById(leadProcessRole.getOrganisationId());
        if (leadOrganisation.isPresent()) {
            result.setLead(leadOrganisation.get().getName());
        }

        if (source.getDecision() != null) {
            result.setDecision(decisionMapper.mapToResource(source.getDecision()));
        }
        if (source.getApplicationProcess().getProcessState() == ApplicationState.APPROVED) {
            result.setDecision(Decision.FUNDED);
        }
        if (source.getProjectToBeCreated() != null && !source.getCompetition().isKtp()) {
            result.setEmailInQueue(source.getProjectToBeCreated().isPending());
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
        result.setEoiEvidenceDocumentRequired(source.expressionOfInterestEvidenceDocumentRequired());
        result.setEoiEvidenceDocumentReceived(source.isApplicationExpressionOfInterestEvidenceResponseReceived());

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
