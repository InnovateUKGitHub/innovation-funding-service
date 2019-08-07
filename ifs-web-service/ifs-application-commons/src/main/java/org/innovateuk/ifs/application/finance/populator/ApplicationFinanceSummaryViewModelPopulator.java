package org.innovateuk.ifs.application.finance.populator;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.innovateuk.ifs.form.resource.SectionType.OVERVIEW_FINANCES;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.getOnlyElementOrEmpty;

@Component
public class ApplicationFinanceSummaryViewModelPopulator {
    @Autowired
    private FinanceService financeService;
    @Autowired
    private FileEntryRestService fileEntryRestService;
    @Autowired
    private OrganisationRestService organisationRestService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private UserRestService userRestService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private CompetitionRestService competitionRestService;

    public ApplicationFinanceSummaryViewModel populate(long applicationId, UserResource user) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );

        SectionResource financeSection = sectionService.getFinanceSection(application.getCompetition());
        final boolean hasFinanceSection = financeSection != null;
        final Long financeSectionId = hasFinanceSection ? financeSection.getId() : null;
        final List<OrganisationResource> applicationOrganisations = getApplicationOrganisations(applicationId);
        OrganisationResource leadOrganisation = organisationService.getLeadOrganisation(applicationId, applicationOrganisations);
        List<SectionResource> eachOrganisationFinanceSections = sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE);

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = getCompletedSectionsForUserOrganisation(completedSectionsByOrganisation, leadOrganisation);
        Long eachCollaboratorFinanceSectionId = getEachCollaboratorFinanceSectionId(eachOrganisationFinanceSections);
        boolean isApplicant = false;

        if (user.hasRole(APPLICANT)) {
            RestResult<ProcessRoleResource> role = userRestService.findProcessRole(user.getId(), applicationId);
            isApplicant = role.isSuccess() && applicantProcessRoles().contains(role.getSuccess().getRole());
        }
        boolean yourFinancesCompleteForAllOrganisations = getFinancesOverviewCompleteForAllOrganisations(
                completedSectionsByOrganisation, application.getCompetition());

        return new ApplicationFinanceSummaryViewModel(
                application,
                hasFinanceSection,
                organisationFinanceOverview.getTotalPerType(),
                applicationOrganisations,
                sectionsMarkedAsComplete,
                financeSectionId,
                leadOrganisation,
                competition,
                getUserOrganisation(user, applicationId),
                organisationFinanceOverview.getFinancesByOrganisation(),
                organisationFinanceOverview.getTotalFundingSought(),
                organisationFinanceOverview.getTotalOtherFunding(),
                organisationFinanceOverview.getTotalContribution(),
                organisationFinanceOverview.getTotal(),
                completedSectionsByOrganisation,
                eachCollaboratorFinanceSectionId,
                yourFinancesCompleteForAllOrganisations,
                application.isCollaborativeProject(),
                isApplicant
        );
    }

    private List<OrganisationResource> getApplicationOrganisations(final Long applicationId) {
        return organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
    }

    private Set<Long> getCompletedSectionsForUserOrganisation(Map<Long, Set<Long>> completedSectionsByOrganisation,
                                                              OrganisationResource userOrganisation) {
        return completedSectionsByOrganisation.getOrDefault(
                userOrganisation.getId(),
                new HashSet<>()
        );
    }

    private boolean getFinancesOverviewCompleteForAllOrganisations(Map<Long, Set<Long>> completedSectionsByOrganisation,
                                                                   Long competitionId) {
        Optional<Long> optionalFinanceOverviewSectionId =
                getOnlyElementOrEmpty(sectionService.getSectionsForCompetitionByType(competitionId,
                        OVERVIEW_FINANCES)).map(SectionResource::getId);

        return optionalFinanceOverviewSectionId
                .map(financeOverviewSectionId -> completedSectionsByOrganisation.values()
                        .stream()
                        .allMatch(completedSections -> completedSections.contains(financeOverviewSectionId)))
                .orElse(false);
    }

    private Long getEachCollaboratorFinanceSectionId(List<SectionResource> eachOrganisationFinanceSections) {
        if (!eachOrganisationFinanceSections.isEmpty()) {
            return eachOrganisationFinanceSections.get(0).getId();
        }

        return null;
    }

    private OrganisationResource getUserOrganisation(UserResource user, Long applicationId) {
        OrganisationResource userOrganisation = null;

        if (!user.isInternalUser() && !user.hasAnyRoles(ASSESSOR, INTERVIEW_ASSESSOR, STAKEHOLDER, MONITORING_OFFICER)) {
            ProcessRoleResource userProcessRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
            userOrganisation = organisationRestService.getOrganisationById(userProcessRole.getOrganisationId()).getSuccess();
        }

        return userOrganisation;
    }
}