package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class ApplicationFinanceSummaryViewModelPopulator {

    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private OrganisationRestService organisationRestService;
    private ApplicationService applicationService;
    private SectionService sectionService;
    private UserRestService userRestService;
    private UserService userService;
    private CompetitionRestService competitionRestService;

    public ApplicationFinanceSummaryViewModelPopulator(ApplicationService applicationService,
                                                       SectionService sectionService,
                                                       FinanceService financeService,
                                                       FileEntryRestService fileEntryRestService,
                                                       OrganisationRestService organisationRestService,
                                                       UserRestService userRestService,
                                                       UserService userService,
                                                       CompetitionRestService competitionRestService) {
        this.applicationService = applicationService;
        this.sectionService = sectionService;
        this.financeService = financeService;
        this.fileEntryRestService = fileEntryRestService;
        this.organisationRestService = organisationRestService;
        this.userRestService = userRestService;
        this.userService = userService;
        this.competitionRestService = competitionRestService;
    }

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
        Long financeSectionId = null;
        if (hasFinanceSection) {
            financeSectionId = financeSection.getId();
        }

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());

        ProcessRoleResource leadApplicantUser = userService.getLeadApplicantProcessRoleOrNull(applicationId);
        OrganisationResource leadOrganisation = organisationRestService.getOrganisationById(leadApplicantUser.getOrganisationId()).getSuccess();

        Set<Long> sectionsMarkedAsComplete = getCompletedSectionsForUserOrganisation(completedSectionsByOrganisation, leadOrganisation);

        List<SectionResource> eachOrganisationFinanceSections = sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE);
        Long eachCollaboratorFinanceSectionId = getEachCollaboratorFinanceSectionId(eachOrganisationFinanceSections);

        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();
        final List<OrganisationResource> applicationOrganisations = getApplicationOrganisations(applicationId);
        final List<OrganisationResource> academicOrganisations = getAcademicOrganisations(applicationOrganisations);
        final List<Long> academicOrganisationIds = academicOrganisations.stream().map(ao -> ao.getId()).collect(Collectors.toList());
        Map<Long, Boolean> applicantOrganisationsAreAcademic = applicationOrganisations.stream().collect(Collectors.toMap(o -> o.getId(), o -> academicOrganisationIds.contains(o.getId())));
        Map<Long, Boolean> showDetailedFinanceLink = applicationOrganisations.stream().collect(Collectors.toMap(OrganisationResource::getId,
                organisation -> {

                    boolean orgFinancesExist = ofNullable(organisationFinances)
                            .map(finances -> organisationFinances.get(organisation.getId()))
                            .map(BaseFinanceResource::getOrganisationSize)
                            .isPresent();
                    boolean academicFinancesExist = applicantOrganisationsAreAcademic.get(organisation.getId());
                    boolean financesExist = orgFinancesExist || academicFinancesExist;

                    return isApplicationVisibleToUser(application, user) && financesExist;
                })
        );

        boolean yourFinancesCompleteForAllOrganisations = getYourFinancesCompleteForAllOrganisations(
                completedSectionsByOrganisation, financeSectionId);

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
                showDetailedFinanceLink,
                yourFinancesCompleteForAllOrganisations
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

    private boolean getYourFinancesCompleteForAllOrganisations(Map<Long, Set<Long>> completedSectionsByOrganisation,
                                                               Long financeSectionId) {
        if (financeSectionId == null) {
            return false;
        }
        return completedSectionsByOrganisation.keySet()
                .stream()
                .noneMatch(id -> !completedSectionsByOrganisation.get(id).contains(financeSectionId));
    }

    private Long getEachCollaboratorFinanceSectionId(List<SectionResource> eachOrganisationFinanceSections) {
        if (!eachOrganisationFinanceSections.isEmpty()) {
            return eachOrganisationFinanceSections.get(0).getId();
        }

        return null;
    }

    private boolean isApplicationVisibleToUser(ApplicationResource application, UserResource user) {
        boolean canSeeUnsubmitted = user.hasRole(IFS_ADMINISTRATOR) || user.hasRole(SUPPORT);
        boolean canSeeSubmitted = user.hasRole(PROJECT_FINANCE) || user.hasRole(COMP_ADMIN) || user.hasRole(INNOVATION_LEAD);
        boolean isSubmitted = application.getApplicationState() != ApplicationState.OPEN &&  application.getApplicationState() != ApplicationState.CREATED;

        return canSeeUnsubmitted || (canSeeSubmitted && isSubmitted);
    }

    private List<OrganisationResource> getAcademicOrganisations(final List<OrganisationResource> organisations) {
        return simpleFilter(organisations, o -> OrganisationTypeEnum.RESEARCH.getId() == o.getOrganisationType());
    }

    private OrganisationResource getUserOrganisation(UserResource user, Long applicationId) {
        OrganisationResource userOrganisation = null;

        if (!user.isInternalUser() && !user.hasAnyRoles(Role.ASSESSOR, Role.INTERVIEW_ASSESSOR)) {
            ProcessRoleResource userProcessRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
            userOrganisation = organisationRestService.getOrganisationById(userProcessRole.getOrganisationId()).getSuccess();
        }

        return userOrganisation;
    }

}