package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.AbstractFinanceModelPopulator;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.form.resource.SectionType.OVERVIEW_FINANCES;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

@Component
public class ApplicationFundingBreakdownViewModelPopulator extends AbstractFinanceModelPopulator {

    private OrganisationRestService organisationRestService;
    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private ApplicationService applicationService;
    private SectionService sectionService;
    private OrganisationService organisationService;
    private InviteService inviteService;
    private UserRestService userRestService;
    private CompetitionRestService competitionRestService;

    public ApplicationFundingBreakdownViewModelPopulator(FinanceService financeService,
                                                         FileEntryRestService fileEntryRestService,
                                                         OrganisationRestService organisationRestService,
                                                         ApplicationService applicationService,
                                                         SectionService sectionService,
                                                         QuestionRestService questionRestService,
                                                         FormInputRestService formInputRestService,
                                                         OrganisationService organisationService,
                                                         InviteService inviteService,
                                                         UserRestService userRestService,
                                                         CompetitionRestService competitionRestService) {
        super(sectionService, formInputRestService, questionRestService);
        this.financeService = financeService;
        this.fileEntryRestService = fileEntryRestService;
        this.organisationRestService = organisationRestService;
        this.applicationService = applicationService;
        this.sectionService = sectionService;
        this.organisationService = organisationService;
        this.inviteService = inviteService;
        this.userRestService = userRestService;
        this.competitionRestService = competitionRestService;
    }

    public ApplicationFundingBreakdownViewModel populate(long applicationId, UserResource user) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        OrganisationResource userOrganisation = getUserOrganisation(user, applicationId);

        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );

        List<OrganisationResource> applicationOrganisations = getApplicationOrganisations(applicationId);
        OrganisationResource leadOrganisation = organisationService.getLeadOrganisation(applicationId, applicationOrganisations);

        SectionResource section = sectionService.getFinanceSection(application.getCompetition());

        final List<String> pendingOrganisationNames = getPendingOrganisationNames(applicationOrganisations, applicationId);

        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();
        final List<OrganisationResource> academicOrganisations = getAcademicOrganisations(applicationOrganisations);

        final List<Long> academicOrganisationIds = simpleMap(academicOrganisations, OrganisationResource::getId);
        final Map<Long, Boolean> applicantOrganisationsAreAcademic = simpleToMap
                (applicationOrganisations, OrganisationResource::getId, o -> academicOrganisationIds.contains(o.getId
                        ()));
        final Map<Long, Boolean> showDetailedFinanceLink = simpleToMap(applicationOrganisations, OrganisationResource::getId,
                organisation -> {
                    boolean orgFinancesExist = ofNullable(organisationFinances)
                            .map(finances -> organisationFinances.get(organisation.getId()))
                            .map(BaseFinanceResource::getOrganisationSize)
                            .isPresent();
                    boolean academicFinancesExist = applicantOrganisationsAreAcademic.get(organisation.getId());
                    boolean financesExist = orgFinancesExist || academicFinancesExist;

                    return isApplicationVisibleToUser(application, user) && financesExist;
                });

        boolean isApplicant = false;
        if (user.hasRole(APPLICANT)) {
            RestResult<ProcessRoleResource> role = userRestService.findProcessRole(user.getId(), applicationId);
            isApplicant = role.isSuccess() && applicantProcessRoles().contains(role.getSuccess().getRole());
        }

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = getCompletedSectionsForUserOrganisation(completedSectionsByOrganisation, leadOrganisation);

        boolean yourFinancesCompleteForAllOrganisations = getFinancesOverviewCompleteForAllOrganisations(
                completedSectionsByOrganisation, application.getCompetition());

        return new ApplicationFundingBreakdownViewModel(
                organisationFinanceOverview.getTotalPerType(),
                applicationOrganisations,
                section,
                section != null ? organisationFinanceOverview.getTotal() : null,
                leadOrganisation,
                organisationFinanceOverview.getFinancesByOrganisation(),
                pendingOrganisationNames,
                application,
                userOrganisation,
                showDetailedFinanceLink,
                application.isCollaborativeProject(),
                isApplicant,
                sectionsMarkedAsComplete,
                completedSectionsByOrganisation,
                yourFinancesCompleteForAllOrganisations,
                competition);

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

    private List<OrganisationResource> getApplicationOrganisations(final Long applicationId) {
        return organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
    }

    private List<String> getPendingOrganisationNames(List<OrganisationResource> applicationOrganisations, Long applicationId) {
        final List<String> activeApplicationOrganisationNames = applicationOrganisations
                .stream()
                .map(OrganisationResource::getName)
                .collect(Collectors.toList());

        return inviteService.getPendingInvitationsByApplicationId(applicationId).stream()
                .map(ApplicationInviteResource::getInviteOrganisationNameConfirmedSafe)
                .distinct()
                .filter(orgName -> StringUtils.hasText(orgName)
                        && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());

    }

    private OrganisationResource getUserOrganisation(UserResource user, Long applicationId) {
        OrganisationResource userOrganisation = null;

        if (!user.isInternalUser() && !user.hasAnyRoles(ASSESSOR, INTERVIEW_ASSESSOR, STAKEHOLDER, MONITORING_OFFICER)) {
            ProcessRoleResource userProcessRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
            userOrganisation = organisationRestService.getOrganisationById(userProcessRole.getOrganisationId()).getSuccess();
        }

        return userOrganisation;
    }

    private List<OrganisationResource> getAcademicOrganisations(final List<OrganisationResource> organisations) {
        return simpleFilter(organisations, o -> OrganisationTypeEnum.RESEARCH.getId() == o.getOrganisationType());
    }

    private boolean isApplicationVisibleToUser(ApplicationResource application, UserResource user) {
        boolean canSeeUnsubmitted = user.hasRole(IFS_ADMINISTRATOR) || user.hasRole(SUPPORT);
        boolean canSeeSubmitted = user.hasRole(PROJECT_FINANCE) || user.hasRole(COMP_ADMIN) || user.hasRole(INNOVATION_LEAD) || user.hasRole(STAKEHOLDER);
        boolean isSubmitted = application.getApplicationState() != ApplicationState.OPENED && application.getApplicationState() != ApplicationState.CREATED;

        return canSeeUnsubmitted || (canSeeSubmitted && isSubmitted);
    }
}