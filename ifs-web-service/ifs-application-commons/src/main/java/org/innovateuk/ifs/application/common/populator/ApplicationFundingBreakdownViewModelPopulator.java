package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.populator.finance.service.FinanceService;
import org.innovateuk.ifs.application.populator.finance.view.AbstractFinanceModelPopulator;
import org.innovateuk.ifs.application.populator.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ApplicationFundingBreakdownViewModelPopulator extends AbstractFinanceModelPopulator {

    private OrganisationRestService organisationRestService;
    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private CompetitionRestService competitionRestService;
    private ApplicationService applicationService;
    private SectionService sectionService;
    private UserService userService;
    private OrganisationService organisationService;
    private InviteService inviteService;

    public ApplicationFundingBreakdownViewModelPopulator(FinanceService financeService,
                                                         FileEntryRestService fileEntryRestService,
                                                         OrganisationRestService organisationRestService,
                                                         CompetitionRestService competitionRestService,
                                                         ApplicationService applicationService,
                                                         SectionService sectionService,
                                                         QuestionService questionService,
                                                         FormInputRestService formInputRestService,
                                                         UserService userService,
                                                         OrganisationService organisationService,
                                                         InviteService inviteService) {
        super(sectionService, formInputRestService, questionService);
        this.financeService = financeService;
        this.fileEntryRestService = fileEntryRestService;
        this.organisationRestService = organisationRestService;
        this.competitionRestService = competitionRestService;
        this.applicationService = applicationService;
        this.sectionService = sectionService;
        this.userService = userService;
        this.organisationService = organisationService;
        this.inviteService = inviteService;
    }

    public ApplicationFundingBreakdownViewModel populate(long applicationId) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        ProcessRoleResource leadApplicantUser = userService.getLeadApplicantProcessRoleOrNull(applicationId);
        OrganisationResource leadOrganisation = organisationService.getOrganisationById(leadApplicantUser.getOrganisationId());

        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );

        List<OrganisationResource> applicationOrganisations = getApplicationOrganisations(applicationId);

        SectionResource section = sectionService.getFinanceSection(competition.getId());

        final List<String> pendingOrganisationNames = getPendingOrganisationNames(applicationOrganisations, applicationId);

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
}
