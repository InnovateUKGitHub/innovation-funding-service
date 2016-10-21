package com.worth.ifs.application.model;

import com.worth.ifs.application.finance.view.FinanceOverviewModelManager;
import com.worth.ifs.application.resource.*;
import com.worth.ifs.application.service.*;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.model.OrganisationDetailsModelPopulator;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class ApplicationPrintPopulator {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Autowired
    private FinanceOverviewModelManager financeOverviewModelManager;


    public String print(final Long applicationId,
                           Model model, HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        List<FormInputResponseResource> responses = formInputResponseService.getByApplication(applicationId);
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);

        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = applicationModelPopulator.getUserOrganisation(user.getId(), userApplicationRoles);
        model.addAttribute("userOrganisation", userOrganisation.orElse(null));

        organisationDetailsModelPopulator.populateModel(model, application.getId(), userApplicationRoles);
        applicationSectionAndQuestionModelPopulator.addQuestionsDetails(model, application, null);
        applicationModelPopulator.addUserDetails(model, application, user.getId());
        applicationModelPopulator.addApplicationInputs(application, model);
        applicationSectionAndQuestionModelPopulator.addMappedSectionsDetails(model, application, competition, Optional.empty(), userOrganisation);
        financeOverviewModelManager.addFinanceDetails(model, competition.getId(), applicationId);

        return "/application/print";
    }

}
