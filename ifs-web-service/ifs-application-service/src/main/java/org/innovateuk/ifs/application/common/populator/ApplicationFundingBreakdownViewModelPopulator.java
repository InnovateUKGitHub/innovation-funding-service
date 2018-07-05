package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.AbstractFinanceModelPopulator;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Component
public class ApplicationFundingBreakdownViewModelPopulator extends AbstractFinanceModelPopulator {

    private OrganisationRestService organisationRestService;
    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private CompetitionService competitionService;
    private ApplicationService applicationService;
    private SectionService sectionService;
    private QuestionService questionService;
    private FormInputRestService formInputRestService;
    private UserService userService;
    private OrganisationService organisationService;
    private InviteRestService inviteRestService;

    public ApplicationFundingBreakdownViewModelPopulator(FinanceService financeService,
                                                         FileEntryRestService fileEntryRestService,
                                                         OrganisationRestService organisationRestService,
                                                         CompetitionService competitionService,
                                                         ApplicationService applicationService,
                                                         SectionService sectionService,
                                                         QuestionService questionService,
                                                         FormInputRestService formInputRestService,
                                                         UserService userService,
                                                         OrganisationService organisationService,
                                                         InviteRestService inviteRestService) {
        super(sectionService, formInputRestService);
        this.financeService = financeService;
        this.fileEntryRestService = fileEntryRestService;
        this.organisationRestService = organisationRestService;
        this.competitionService = competitionService;
        this.applicationService = applicationService;
        this.sectionService = sectionService;
        this.questionService = questionService;
        this.userService = userService;
        this.organisationService = organisationService;
        this.inviteRestService = inviteRestService;
    }

    public ApplicationFundingBreakdownViewModel populate(long applicationId) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        ProcessRoleResource leadApplicantUser = userService.getLeadApplicantProcessRoleOrNull(applicationId);
        OrganisationResource leadOrganisation = organisationService.getOrganisationById(leadApplicantUser.getOrganisationId());

        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );

        List<OrganisationResource> applicationOrganisations = getApplicationOrganisations(applicationId);

        SectionResource section = sectionService.getFinanceSection(competition.getId());

        final List<String> activeApplicationOrganisationNames = applicationOrganisations
                .stream()
                .map(OrganisationResource::getName)
                .collect(Collectors.toList());

        final List<String> pendingOrganisationNames = pendingInvitations(applicationId).stream()
                .map(ApplicationInviteResource::getInviteOrganisationNameConfirmedSafe)
                .distinct()
                .filter(orgName -> StringUtils.hasText(orgName)
                        && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());

        // Finance Section will be null for EOI Competitions
        if (section != null) {
            sectionService.removeSectionsQuestionsWithType(section, FormInputType.EMPTY);
            List<SectionResource> financeSubSectionChildren = getFinanceSubSectionChildren(competition.getId(), section);


            List<QuestionResource> allQuestions = questionService.findByCompetition(competition.getId());

            Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap = financeSubSectionChildren.stream()
                    .collect(toMap(
                            SectionResource::getId,
                            s -> filterQuestions(s.getQuestions(), allQuestions)
                    ));

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
                    pendingOrganisationNames

            );
        } else {
            return new ApplicationFundingBreakdownViewModel(
                    organisationFinanceOverview.getTotalPerType(),
                    applicationOrganisations,
                    section,
                    leadOrganisation,
                    organisationFinanceOverview.getFinancesByOrganisation(),
                    pendingOrganisationNames

            );
        }

    }

    private List<OrganisationResource> getApplicationOrganisations(final Long applicationId) {
        return organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
    }

    private List<ApplicationInviteResource> pendingInvitations(final Long applicationId) {
        final RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(applicationId);

        return pendingAssignableUsersResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(0),
                success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                        .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                        .collect(Collectors.toList()));
    }
}
