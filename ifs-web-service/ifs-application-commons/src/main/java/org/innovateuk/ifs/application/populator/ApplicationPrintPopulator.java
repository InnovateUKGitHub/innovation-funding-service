package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.finance.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class ApplicationPrintPopulator {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Autowired
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;


    public String print(final Long applicationId,
                           Model model, UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(applicationId).getSuccess();
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);

        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = applicationModelPopulator.getUserOrganisation(user.getId(), userApplicationRoles);
        model.addAttribute("userOrganisation", userOrganisation.orElse(null));

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());

        organisationDetailsModelPopulator.populateModel(model, application.getId(), userApplicationRoles);
        applicationSectionAndQuestionModelPopulator.addQuestionsDetails(model, application, null);
        applicationModelPopulator.addUserDetails(model, user, userApplicationRoles);
        applicationModelPopulator.addApplicationInputs(application, model);
        applicationSectionAndQuestionModelPopulator.addMappedSectionsDetails(model, application, competition, Optional.empty(), userOrganisation, user.getId(), completedSectionsByOrganisation, Optional.empty());
        applicationFinanceOverviewModelManager.addFinanceDetails(model, competition.getId(), applicationId);

        return "application/print";
    }
}
