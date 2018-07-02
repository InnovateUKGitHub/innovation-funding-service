package org.innovateuk.ifs.application.common.populator;


import org.innovateuk.ifs.application.common.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ApplicationFinanceSummaryViewModelPopulator {

    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private OrganisationRestService organisationRestService;
    private ApplicationService applicationService;
    private SectionService sectionService;
    private OrganisationService organisationService;
    private ProcessRoleService processRoleService;
    private UserService userService;
    private CompetitionService competitionService;

    public ApplicationFinanceSummaryViewModelPopulator(ApplicationService applicationService,
                                                       SectionService sectionService,
                                                       FinanceService financeService,
                                                       FileEntryRestService fileEntryRestService,
                                                       OrganisationRestService organisationRestService,
                                                       OrganisationService organisationService,
                                                       ProcessRoleService processRoleService,
                                                       UserService userService,
                                                       CompetitionService competitionService) {
        this.applicationService = applicationService;
        this.sectionService = sectionService;
        this.financeService = financeService;
        this.fileEntryRestService = fileEntryRestService;
        this.organisationRestService = organisationRestService;
        this.organisationService = organisationService;
        this.processRoleService = processRoleService;
        this.userService = userService;
        this.competitionService = competitionService;
    }

    public ApplicationFinanceSummaryViewModel populate(long applicationId, UserResource user) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );

        BigDecimal totalFundingSought = organisationFinanceOverview.getTotalFundingSought();
        BigDecimal totalOtherFunding = organisationFinanceOverview.getTotalOtherFunding();
        BigDecimal totalContribution = organisationFinanceOverview.getTotalContribution();
        BigDecimal financeTotal = organisationFinanceOverview.getTotal();

        Map<FinanceRowType, BigDecimal> financeTotalPerType = organisationFinanceOverview.getTotalPerType();
        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();

        List<OrganisationResource> applicationOrganisations = getApplicationOrganisations(applicationId);

        SectionResource financeSection = sectionService.getFinanceSection(application.getCompetition());
        final boolean hasFinanceSection;
        final Long financeSectionId;
        if (financeSection == null) {
            hasFinanceSection = false;
            financeSectionId = null;
        } else {
            hasFinanceSection = true;
            financeSectionId = financeSection.getId();
        }

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());

        ProcessRoleResource leadApplicantUser = userService.getLeadApplicantProcessRoleOrNull(applicationId);
        OrganisationResource leadOrganisation = organisationService.getOrganisationById(leadApplicantUser.getOrganisationId());

        Set<Long> sectionsMarkedAsComplete = getCompletedSectionsForUserOrganisation(completedSectionsByOrganisation, leadOrganisation);

        OrganisationResource userOrganisation = null;

        if (!user.isInternalUser() && !user.hasAnyRoles(Role.ASSESSOR, Role.INTERVIEW_ASSESSOR)) {
            ProcessRoleResource userProcessRole = processRoleService.findProcessRole(user.getId(), applicationId);
            userOrganisation = organisationService.getOrganisationById(userProcessRole.getOrganisationId());
        }

        return new ApplicationFinanceSummaryViewModel(
                application,
                hasFinanceSection,
                financeTotalPerType,
                applicationOrganisations,
                sectionsMarkedAsComplete,
                financeSectionId,
                leadOrganisation,
                competition,
                userOrganisation,
                organisationFinances,
                totalFundingSought,
                totalOtherFunding,
                totalContribution,
                financeTotal,
                completedSectionsByOrganisation
                );
    }

    private List<OrganisationResource> getApplicationOrganisations(final Long applicationId) {
        return organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
    }

    private Set<Long> getCompletedSectionsForUserOrganisation(Map<Long, Set<Long>> completedSectionsByOrganisation, OrganisationResource userOrganisation) {
        return completedSectionsByOrganisation.getOrDefault(
                userOrganisation.getId(),
                new HashSet<>()
        );
    }

}
