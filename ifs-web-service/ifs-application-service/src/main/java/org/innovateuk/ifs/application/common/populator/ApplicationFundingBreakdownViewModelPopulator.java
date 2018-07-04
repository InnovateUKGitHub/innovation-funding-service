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
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

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
        super(sectionService);
        this.financeService = financeService;
        this.fileEntryRestService = fileEntryRestService;
        this.organisationRestService = organisationRestService;
        this.competitionService = competitionService;
        this.applicationService = applicationService;
        this.sectionService = sectionService;
        this.questionService = questionService;
        this.formInputRestService = formInputRestService;
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

        Map<FinanceRowType, BigDecimal> financeTotalPerType = organisationFinanceOverview.getTotalPerType();
        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();
        BigDecimal financeTotal = organisationFinanceOverview.getTotal();

        List<OrganisationResource> applicationOrganisations = getApplicationOrganisations(applicationId);

        SectionResource section = sectionService.getFinanceSection(competition.getId());

        final List<String> activeApplicationOrganisationNames = applicationOrganisations.stream().map(OrganisationResource::getName).collect(Collectors.toList());

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

            List<FormInputResource> formInputs = formInputRestService.getByCompetitionIdAndScope(
                    competition.getId(),
                    APPLICATION
            )
                    .getSuccess();

            Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs = financeSectionChildrenQuestionsMap
                    .values().stream().flatMap(a -> a.stream())
                    .collect(toMap(q -> q.getId(), k -> filterFormInputsByQuestion(k.getId(), formInputs)));


            //Remove all questions without non-empty form inputs.
            Set<Long> questionsWithoutNonEmptyFormInput = financeSectionChildrenQuestionFormInputs.keySet().stream()
                    .filter(key -> financeSectionChildrenQuestionFormInputs.get(key).isEmpty()).collect(Collectors.toSet());
            questionsWithoutNonEmptyFormInput.forEach(questionId -> {
                financeSectionChildrenQuestionFormInputs.remove(questionId);
                    financeSectionChildrenQuestionsMap.keySet().forEach(key -> financeSectionChildrenQuestionsMap.get(key)
                    .removeIf(questionResource -> questionResource.getId().equals(questionId)));
            });

            return new ApplicationFundingBreakdownViewModel(
                    financeTotalPerType,
                    financeTotal,
                    applicationOrganisations,
                    section,
                    financeSubSectionChildren,
                    financeSectionChildrenQuestionsMap,
                    financeSectionChildrenQuestionFormInputs,
                    leadOrganisation,
                    organisationFinances,
                    pendingOrganisationNames

            );
        } else {
            return new ApplicationFundingBreakdownViewModel(
                    financeTotalPerType,
                    applicationOrganisations,
                    section,
                    leadOrganisation,
                    organisationFinances,
                    pendingOrganisationNames

            );
        }

    }

    private List<OrganisationResource> getApplicationOrganisations(final Long applicationId) {
        return organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
    }

    private List<QuestionResource> filterQuestions(final List<Long> ids, final List<QuestionResource> list) {
        return simpleFilter(list, question -> ids.contains(question.getId()));
    }

    private List<FormInputResource> filterFormInputsByQuestion(final Long id, final List<FormInputResource> list) {
        return simpleFilter(list,
                input -> id.equals(input.getQuestion()) && !FormInputType.EMPTY.equals(input.getType()));
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
