package org.innovateuk.ifs.grant.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckServiceImpl;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.sil.grant.resource.Forecast;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.innovateuk.ifs.sil.grant.resource.Period;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterFinanceTotalsTablePopulator.GRANT_CLAIM_IDENTIFIER;
import static org.innovateuk.ifs.user.resource.Role.FINANCE_CONTACT;

@Service
public class GrantServiceImpl implements GrantService {
    private static final Log LOG = LogFactory.getLog(GrantServiceImpl.class);
    private static final String NO_PROJECT_SUMMARY = "no project summary";
    private static final String NO_PUBLIC_DESCRIPTION = "no public description";

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private GrantEndpoint grantEndpoint;

    @Override
    @Transactional
    public ServiceResult<Void> sendProject(Long applicationId) {
        LOG.info("Sending project : " + applicationId);
        Project project = projectRepository.findOneByApplicationId(applicationId);
        Grant grant = new Grant();
        grant.setId(project.getId());
        grant.setCompetitionCode(project.getApplication().getCompetition().getCode());
        grant.setTitle(project.getName());
        grant.setGrantOfferLetterDate(project.getOfferSubmittedDate());
        grant.setStartDate(project.getTargetStartDate());
        grant.setDuration(project.getDurationInMonths());
        List<FormInputResponse> formInputResponses = formInputResponseRepository.findByApplicationId(applicationId);
        grant.setSummary(formInputResponses.stream()
                .filter(response -> "project summary"
                        .equals(response.getFormInput().getDescription()))
                .findFirst()
                .map(FormInputResponse::getValue)
                .orElse(NO_PROJECT_SUMMARY));
        grant.setSummary(formInputResponses.stream()
                .filter(response -> "public description"
                        .equals(response.getFormInput().getDescription()))
                .findFirst()
                .map(FormInputResponse::getValue)
                .orElse(NO_PUBLIC_DESCRIPTION));

        Context context = new Context()
                .withProjectId(project.getId())
                .withApplicationId(applicationId)
                .withStartDate(project.getTargetStartDate());
        grant.setParticipants(
                project.getPartnerOrganisations().stream()
                        .map(o -> toParticipant(context, o))
                        .collect(Collectors.toSet())
        );
        grantEndpoint.send(grant);
        return serviceSuccess();
    }

    private Participant toParticipant(Context context, PartnerOrganisation partnerOrganisation) {
        Participant participant = new Participant();
        Organisation organisation = partnerOrganisation.getOrganisation();
        participant.setId(organisation.getId());
        participant.setOrgType(organisation.getOrganisationType().getName());
        participant.setOrgProjectRole(partnerOrganisation.isLeadOrganisation() ? "lead" : "collaborator");
        participant.setSize(organisation.getUsers().size());
        organisation.getProcessRoles().stream()
                .filter(role -> role.getRole() == FINANCE_CONTACT)
                .findFirst()
                .ifPresent(processRole -> {
                            participant.setContactEmail(processRole.getUser().getEmail());
                            participant.setContactRole(processRole.getRole().getName());
                        }
                );
        Optional<SpendProfile> spendProfile = spendProfileRepository
                .findOneByProjectIdAndOrganisationId(context.getProjectId(), organisation.getId());
        if (!spendProfile.isPresent()) {
            throw new IllegalStateException("Project " + context.getProjectId() + " and organisation "
                    + organisation.getId() + " does not have a spend profile.  All organisations MUST "
                    + "have a spend profile to send grant");
        }
        /*
         * Calculate overhead percentage
         */
        SpendProfileCalculations grantCalculator = new SpendProfileCalculations(spendProfile.get());
        participant.setOverheadRate(grantCalculator.getOverheadPercentage());

        /*
         * Get cap limit
         */
        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(
                context.getApplicationId(), organisation.getId()
        );
        participant.setCapLimit(BigDecimal.valueOf(applicationFinance.getMaximumFundingLevel()));

        /*
         * Calculate award
         */
        ProjectFinance projectFinance = projectFinanceRepository
                .findByProjectIdAndOrganisationId(context.getProjectId(), organisation.getId());
        List<ProjectFinanceRow> projectFinanceRows = projectFinanceRowRepository.findByTargetId(projectFinance.getId());
        BigDecimal awardPercentage = projectFinanceRows.stream()
                .filter(row -> GRANT_CLAIM_IDENTIFIER.equals(row.getName()))
                .findFirst()
                .map(row -> BigDecimal.valueOf(row.getQuantity()))
                .orElseThrow(IllegalStateException::new);
        participant.setAwardRate(awardPercentage);

        participant.setForecasts(spendProfile.get()
                .getSpendProfileFigures()
                .getCosts().stream()
                .collect(
                    groupingBy(cost -> cost.getCostCategory().getName())
                ).values().stream()
                .map(this::toForecast)
                .collect(Collectors.toSet())
        );
        return participant;
    }

    private Forecast toForecast(List<Cost> costs) {
        Forecast forecast = new Forecast();
        forecast.setCostCategory(costs.stream()
                .findFirst()
                .orElseThrow(IllegalStateException::new)
                .getCostCategory().getName());
        forecast.setPeriods(costs.stream().map(this::toPeriod).collect(Collectors.toSet()));
        return forecast;
    }

    private Period toPeriod(Cost cost) {
        Period period = new Period();
        period.setMonth(cost.getCostTimePeriod().getOffsetAmount());
        period.setValue(cost.getValue());
        return period;
    }

    @Override
    @Transactional
    public ServiceResult<Void> sendReadyProjects() {
        List<Project> readyProjects = projectRepository.findReadyToSend();
        LOG.info("Sending " + readyProjects.size() + " projects");
        readyProjects.forEach(it -> sendProject(it.getApplication().getId()));
        return serviceSuccess();
    }

    private static class Context {
        private long projectId;
        private long applicationId;
        private LocalDate startDate;

        Context withProjectId(long projectId) {
            this.projectId = projectId;
            return this;
        }

        Context withApplicationId(long applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        Context withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        long getProjectId() {
            return projectId;
        }

        long getApplicationId() {
            return applicationId;
        }

        LocalDate getStartDate() {
            return startDate;
        }
    }
}
