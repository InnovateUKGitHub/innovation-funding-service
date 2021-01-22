package org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator;

import org.innovateuk.ifs.application.ApplicationUrlHelper;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel.ApplicationProcurementMilestonesViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationProcurementMilestoneViewModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private QuestionRestService questionRestService;

    public ApplicationProcurementMilestonesViewModel populate(UserResource user, long applicationId, long organisationId, long sectionId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);
        QuestionResource applicationDetails = questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), QuestionSetupType.APPLICATION_DETAILS).getSuccess();
        SectionResource projectCostsSection = sectionRestService.getSectionsByCompetitionIdAndType(competition.getId(), SectionType.PROJECT_COST_FINANCES).getSuccess().stream().findAny().orElseThrow(ObjectNotFoundException::new);

        boolean userCanEdit = user.hasRole(Role.APPLICANT) && processRoleRestService.findProcessRole(user.getId(), applicationId).getOptionalSuccessObject()
                .map(role -> role.getOrganisationId() != null && role.getOrganisationId().equals(organisationId))
                .orElse(false);
        boolean open = userCanEdit && application.isOpen() && competition.isOpen() && application.getDurationInMonths() != null;

        boolean complete = completedSectionIds.contains(sectionId);
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        return new ApplicationProcurementMilestonesViewModel(application,
                finance,
                String.format("/application/%d/form/FINANCE/%d", applicationId, organisationId),
                complete,
                open,
                ApplicationUrlHelper.getQuestionUrl(QuestionSetupType.APPLICATION_DETAILS, applicationDetails.getId(), applicationId).orElseThrow(ObjectNotFoundException::new),
                completedSectionIds.contains(projectCostsSection.getId()));
    }
}
