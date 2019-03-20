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
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

@Component
public class ApplicationFundingBreakdownViewModelPopulator extends AbstractFinanceModelPopulator {

    private OrganisationRestService organisationRestService;
    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private CompetitionRestService competitionRestService;
    private ApplicationService applicationService;
    private SectionService sectionService;
    private UserService userService;
    private InviteService inviteService;
    private UserRestService userRestService;

    public ApplicationFundingBreakdownViewModelPopulator(FinanceService financeService,
                                                         FileEntryRestService fileEntryRestService,
                                                         OrganisationRestService organisationRestService,
                                                         CompetitionRestService competitionRestService,
                                                         ApplicationService applicationService,
                                                         SectionService sectionService,
                                                         QuestionRestService questionRestService,
                                                         FormInputRestService formInputRestService,
                                                         UserService userService,
                                                         InviteService inviteService,
                                                         UserRestService userRestService) {
        super(sectionService, formInputRestService, questionRestService);
        this.financeService = financeService;
        this.fileEntryRestService = fileEntryRestService;
        this.organisationRestService = organisationRestService;
        this.competitionRestService = competitionRestService;
        this.applicationService = applicationService;
        this.sectionService = sectionService;
        this.userService = userService;
        this.inviteService = inviteService;
        this.userRestService = userRestService;
    }

    public ApplicationFundingBreakdownViewModel populate(long applicationId, UserResource user) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        ProcessRoleResource leadApplicantUser = userService.getLeadApplicantProcessRoleOrNull(applicationId);
        OrganisationResource leadOrganisation = organisationRestService.getOrganisationById(leadApplicantUser.getOrganisationId()).getSuccess();

        OrganisationResource userOrganisation = getUserOrganisation(user, applicationId);

        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );

        List<OrganisationResource> applicationOrganisations = getApplicationOrganisations(applicationId);

        SectionResource section = sectionService.getFinanceSection(competition.getId());

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

        // Finance Section will be null for EOI Competitions
        if (section != null) {
            sectionService.removeSectionsQuestionsWithType(section, FormInputType.EMPTY);
            List<SectionResource> financeSubSectionChildren = getFinanceSubSectionChildren(competition.getId(), section);

            Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap =
                    getFinanceSectionChildrenQuestionsMap(financeSubSectionChildren, competition.getId());

            Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs =
                    getFinanceSectionChildrenQuestionFormInputs(competition.getId(), financeSectionChildrenQuestionsMap);

            return new ApplicationFundingBreakdownViewModel(
                    organisationFinanceOverview.getTotalPerType(),
                    organisationFinanceOverview.getTotal(),
                    applicationOrganisations,
                    section,
                    financeSubSectionChildren,
                    financeSectionChildrenQuestionsMap,
                    financeSectionChildrenQuestionFormInputs,
                    leadOrganisation,
                    organisationFinanceOverview.getFinancesByOrganisation(),
                    pendingOrganisationNames,
                    application,
                    userOrganisation,
                    showDetailedFinanceLink);
        } else {
            return new ApplicationFundingBreakdownViewModel(
                    organisationFinanceOverview.getTotalPerType(),
                    applicationOrganisations,
                    section,
                    leadOrganisation,
                    organisationFinanceOverview.getFinancesByOrganisation(),
                    pendingOrganisationNames,
                    application,
                    userOrganisation,
                    showDetailedFinanceLink);
        }

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
        boolean isSubmitted = application.getApplicationState() != ApplicationState.OPEN && application.getApplicationState() != ApplicationState.CREATED;

        return canSeeUnsubmitted || (canSeeSubmitted && isSubmitted);
    }
}