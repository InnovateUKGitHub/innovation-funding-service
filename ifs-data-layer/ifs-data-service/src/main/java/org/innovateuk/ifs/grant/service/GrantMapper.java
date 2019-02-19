
package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.sil.grant.resource.Forecast;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.innovateuk.ifs.sil.grant.resource.Period;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterFinanceTotalsTablePopulator.GRANT_CLAIM_IDENTIFIER;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

@Component
class GrantMapper {

    private static final String NO_PROJECT_SUMMARY = "no project summary";
    private static final String NO_PUBLIC_DESCRIPTION = "no public description";
    private static final String ACADEMIC_ORGANISATION_SIZE_VALUE = "ACADEMIC";

    private static final Map<Role, String> IFS_ROLES_TO_LIVE_ROLE_NAMES = asMap(
            PROJECT_FINANCE_CONTACT.name(), "Finance contact",
            PROJECT_MANAGER.name(), "Project manager",
            Role.INNOVATION_LEAD.name(), "Innovation lead",
            MONITORING_OFFICER.name(), "Monitoring officer"
    );

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private InnovationLeadRepository innovationLeadRepository;

    public Grant mapToGrant(Project project) {

        long applicationId = project.getApplication().getId();
        long competitionId = project.getApplication().getCompetition().getId();

        Grant grant = new Grant();
        grant.setSourceSystem("IFS");
        grant.setId(applicationId);
        grant.setCompetitionCode(competitionId);
        grant.setTitle(project.getName());
        grant.setGrantOfferLetterDate(project.getOfferSubmittedDate());
        grant.setStartDate(project.getTargetStartDate());
        grant.setDuration(project.getDurationInMonths());

        FormInputResponse summaryResponse =
                formInputResponseRepository.findOneByApplicationIdAndFormInputDescription(applicationId, "Project summary");
        grant.setSummary(summaryResponse != null ? summaryResponse.getValue() : NO_PROJECT_SUMMARY);

        FormInputResponse publicDescriptionResponse =
                formInputResponseRepository.findOneByApplicationIdAndFormInputDescription(applicationId, "Public description");
        grant.setPublicDescription(publicDescriptionResponse != null ? publicDescriptionResponse.getValue() : NO_PUBLIC_DESCRIPTION);

        List<Participant> financeContactParticipants = simpleMap(project.getPartnerOrganisations(), partnerOrganisation -> {

            ProjectUser financeContact = simpleFindFirstMandatory(project.getProjectUsers(), projectUser ->
                    projectUser.getOrganisation().getId().equals(partnerOrganisation.getOrganisation().getId()) &&
                            projectUser.getRole().isFinanceContact());

            return toProjectTeamParticipant(project, partnerOrganisation, financeContact);
        });

        PartnerOrganisation leadOrganisation =
                simpleFindFirstMandatory(project.getPartnerOrganisations(), PartnerOrganisation::isLeadOrganisation);

        ProjectUser projectManager =
                getOnlyElement(project.getProjectUsersWithRole(PROJECT_MANAGER));

        Participant projectManagerParticipant = toProjectTeamParticipant(project, leadOrganisation, projectManager);

        InnovationLead innovationLead = innovationLeadRepository.getByCompetitionIdAndRole(competitionId, CompetitionParticipantRole.INNOVATION_LEAD).get(0);
        User innovationLeadUser = innovationLead.getUser();

        Participant innovationLeadParticipant = toSimpleContactParticipant(
                innovationLeadUser.getId(),
                Role.INNOVATION_LEAD,
                innovationLeadUser.getEmail());

        List<Participant> monitoringOfficerParticipants = simpleMap(
                project.getProjectMonitoringOfficers(),
                mo -> toSimpleContactParticipant(
                        mo.getUser().getId(), Role.MONITORING_OFFICER, mo.getUser().getEmail()
                )
        );

        List<Participant> fullParticipantList = combineLists(
                financeContactParticipants,
                singletonList(projectManagerParticipant),
                singletonList(innovationLeadParticipant),
                monitoringOfficerParticipants
        );

        grant.setParticipants(fullParticipantList);

        return grant;
    }

    private Participant toProjectTeamParticipant(
            Project project,
            PartnerOrganisation partnerOrganisation,
            ProjectUser contactUser) {

        Organisation organisation = partnerOrganisation.getOrganisation();

        Optional<SpendProfile> spendProfile = spendProfileRepository
                .findOneByProjectIdAndOrganisationId(project.getId(), organisation.getId());

        if (!spendProfile.isPresent()) {
            throw new IllegalStateException("Project " + project.getId() + " and organisation "
                    + organisation.getId() + " does not have a spend profile.  All organisations MUST "
                    + "have a spend profile to send grant");
        }

        /*
         * Calculate overhead percentage
         */
        SpendProfileCalculations grantCalculator = new SpendProfileCalculations(spendProfile.get());

        /*
         * Get cap limit
         */
        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(
                project.getApplication().getId(), organisation.getId()
        );

        /*
         * Calculate award
         */
        ProjectFinance projectFinance = projectFinanceRepository
                .findByProjectIdAndOrganisationId(project.getId(), organisation.getId());

        String organisationSizeOrAcademic = projectFinance.getOrganisationSize() != null ?
                projectFinance.getOrganisationSize().name() : ACADEMIC_ORGANISATION_SIZE_VALUE;

        List<ProjectFinanceRow> projectFinanceRows = projectFinanceRowRepository.findByTargetId(projectFinance.getId());
        ProjectFinanceRow awardRow = simpleFindFirstMandatory(projectFinanceRows, row -> GRANT_CLAIM_IDENTIFIER.equals(row.getName()));
        BigDecimal awardPercentage = BigDecimal.valueOf(awardRow.getQuantity());

        List<Forecast> forecasts = contactUser.isFinanceContact() ? spendProfile.get()
                .getSpendProfileFigures()
                .getCosts().stream()
                .sorted(Comparator.comparing(this::getFullCostCategoryName))
                .collect(groupingBy(this::getFullCostCategoryName))
                .values().stream()
                .map(this::toForecast)
                .collect(Collectors.toList()) :
                null;

        return Participant.createProjectTeamParticipant(
                organisation.getId(),
                organisation.getOrganisationType().getName(),
                partnerOrganisation.isLeadOrganisation() ? "lead" : "collaborator",
                contactUser.getUser().getId(),
                lookupLiveProjectsRoleName(contactUser.getRole().name()),
                contactUser.getUser().getEmail(),
                organisationSizeOrAcademic,
                BigDecimal.valueOf(applicationFinance.getMaximumFundingLevel()),
                awardPercentage,
                grantCalculator.getOverheadPercentage(),
                forecasts);
    }

    private Participant toSimpleContactParticipant(
            long userId,
            Role role,
            String userEmail) {

        return Participant.createSimpleContactParticipant(
                userId,
                lookupLiveProjectsRoleName(role.name()),
                userEmail);
    }

    private String lookupLiveProjectsRoleName(String role) {
        return IFS_ROLES_TO_LIVE_ROLE_NAMES.getOrDefault(role, role);
    }

    private String getFullCostCategoryName(Cost cost) {
        CostCategory category = cost.getCostCategory();
        return !isBlank(category.getLabel()) ?
                category.getLabel() + " - " + category.getName() :
                category.getName();
    }

    private Forecast toForecast(List<Cost> costs) {
        Forecast forecast = new Forecast();
        forecast.setCostCategory(getFullCostCategoryName(costs.stream()
                .findFirst()
                .orElseThrow(IllegalStateException::new)));
        forecast.setCost(costs.stream()
                .map(Cost::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP).longValue()
        );
        forecast.setPeriods(costs
                .stream()
                .sorted(Comparator.comparing(cost -> cost.getCostTimePeriod().getOffsetAmount()))
                .map(this::toPeriod)
                .collect(Collectors.toList()));
        return forecast;
    }

    private Period toPeriod(Cost cost) {
        Period period = new Period();
        period.setMonth(cost.getCostTimePeriod().getOffsetAmount());
        period.setValue(cost.getValue()
                .setScale(0, RoundingMode.HALF_UP).longValue());
        return period;
    }
}