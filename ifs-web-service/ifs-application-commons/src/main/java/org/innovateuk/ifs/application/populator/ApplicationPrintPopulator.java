package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

@Component
public class ApplicationPrintPopulator {

    private ApplicationService applicationService;
    private SectionService sectionService;
    private CompetitionRestService competitionRestService;
    private QuestionRestService questionRestService;
    private FormInputResponseService formInputResponseService;
    private FormInputResponseRestService formInputResponseRestService;
    private ProcessRoleService processRoleService;
    private ApplicationModelPopulator applicationModelPopulator;
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    public ApplicationPrintPopulator(ApplicationService applicationService,
                                     SectionService sectionService,
                                     CompetitionRestService competitionRestService,
                                     QuestionRestService questionRestService,
                                     FormInputResponseService formInputResponseService,
                                     FormInputResponseRestService formInputResponseRestService,
                                     ProcessRoleService processRoleService,
                                     ApplicationModelPopulator applicationModelPopulator,
                                     ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator,
                                     OrganisationDetailsModelPopulator organisationDetailsModelPopulator,
                                     ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager) {
        this.applicationService = applicationService;
        this.sectionService = sectionService;
        this.competitionRestService = competitionRestService;
        this.questionRestService = questionRestService;
        this.formInputResponseService = formInputResponseService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.processRoleService = processRoleService;
        this.applicationModelPopulator = applicationModelPopulator;
        this.applicationSectionAndQuestionModelPopulator = applicationSectionAndQuestionModelPopulator;
        this.organisationDetailsModelPopulator = organisationDetailsModelPopulator;
        this.applicationFinanceOverviewModelManager = applicationFinanceOverviewModelManager;
    }

    public String print(final Long applicationId,
                        Model model, UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(applicationId).getSuccess();
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("researchCategoryRequired", researchCategoryRequired(competition.getId()));

        List<ProcessRoleResource> userApplicationRoles = userRestService.findProcessRole(application.getId()).getSuccess();
        Optional<OrganisationResource> userOrganisation = applicationModelPopulator.getUserOrganisation(user.getId(), userApplicationRoles);
        model.addAttribute("userOrganisation", userOrganisation.orElse(null));

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());

        organisationDetailsModelPopulator.populateModel(model, application.getId(), userApplicationRoles);
        applicationSectionAndQuestionModelPopulator.addQuestionsDetails(model, application, null);
        applicationModelPopulator.addUserDetails(model, user, userApplicationRoles);
        applicationSectionAndQuestionModelPopulator.addMappedSectionsDetails(model, application, competition, Optional.empty(), userOrganisation, user.getId(), completedSectionsByOrganisation, Optional.empty());
        applicationFinanceOverviewModelManager.addFinanceDetails(model, competition.getId(), applicationId);

        return "application/print";
    }

    private boolean researchCategoryRequired(long competitionId) {
        return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, RESEARCH_CATEGORY)
                .isSuccess();
    }
}
