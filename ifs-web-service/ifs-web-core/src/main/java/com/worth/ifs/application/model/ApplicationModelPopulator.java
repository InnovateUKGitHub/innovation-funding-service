package com.worth.ifs.application.model;

import com.worth.ifs.application.finance.view.FinanceHandler;
import com.worth.ifs.application.finance.view.FinanceOverviewModelManager;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.*;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;

@Component
public class ApplicationModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    protected UserService userService;

    @Autowired
    protected QuestionService questionService;

    @Autowired
    protected ProcessRoleService processRoleService;

    @Autowired
    protected SectionService sectionService;

    @Autowired
    protected FinanceOverviewModelManager financeOverviewModelManager;

    @Autowired
    protected OrganisationService organisationService;

    @Autowired
    protected FinanceHandler financeHandler;

    @Autowired
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    public ApplicationResource addApplicationAndSections(ApplicationResource application,
                                                         CompetitionResource competition,
                                                         Long userId,
                                                         Optional<SectionResource> section,
                                                         Optional<Long> currentQuestionId,
                                                         Model model,
                                                         ApplicationForm form) {

        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        application = addApplicationDetails(application, competition, userId, section, currentQuestionId, model, form, userApplicationRoles);

        model.addAttribute("completedQuestionsPercentage", application.getCompletion());
        applicationSectionAndQuestionModelPopulator.addSectionDetails(model, section);

        return application;
    }

    public ApplicationResource addApplicationDetails(ApplicationResource application,
                                                        CompetitionResource competition,
                                                        Long userId,
                                                        Optional<SectionResource> section,
                                                        Optional<Long> currentQuestionId,
                                                        Model model,
                                                        ApplicationForm form,
                                                        List<ProcessRoleResource> userApplicationRoles) {
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);

        Optional<OrganisationResource> userOrganisation = getUserOrganisation(userId, userApplicationRoles);
        model.addAttribute("userOrganisation", userOrganisation.orElse(null));

        if(form == null){
            form = new ApplicationForm();
        }
        form.setApplication(application);

        applicationSectionAndQuestionModelPopulator.addQuestionsDetails(model, application, form);
        addUserDetails(model, application, userId);
        addApplicationFormDetailInputs(application, form);
        applicationSectionAndQuestionModelPopulator.addMappedSectionsDetails(model, application, competition, section, userOrganisation);

        applicationSectionAndQuestionModelPopulator.addAssignableDetails(model, application, userOrganisation.orElse(null), userId, section, currentQuestionId);
        applicationSectionAndQuestionModelPopulator.addCompletedDetails(model, application, userOrganisation);

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);
        return application;
    }


    public void addApplicationFormDetailInputs(ApplicationResource application, Form form) {
        Map<String, String> formInputs = form.getFormInput();
        formInputs.put("application_details-title", application.getName());
        formInputs.put("application_details-duration", String.valueOf(application.getDurationInMonths()));
        if(application.getStartDate() == null){
            formInputs.put("application_details-startdate_day", "");
            formInputs.put("application_details-startdate_month", "");
            formInputs.put("application_details-startdate_year", "");
        }else{
            formInputs.put("application_details-startdate_day", String.valueOf(application.getStartDate().getDayOfMonth()));
            formInputs.put("application_details-startdate_month", String.valueOf(application.getStartDate().getMonthValue()));
            formInputs.put("application_details-startdate_year", String.valueOf(application.getStartDate().getYear()));
        }
        form.setFormInput(formInputs);
    }


    public void addUserDetails(Model model, ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = userIsLeadApplicant(application, userId);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());

        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
        model.addAttribute("leadApplicant", leadApplicant);
    }

    public boolean userIsLeadApplicant(ApplicationResource application, Long userId) {
        return userService.isLeadApplicant(userId, application);
    }

    public void addOrganisationAndUserFinanceDetails(Long competitionId, Long applicationId, UserResource user,
                                                     Model model, ApplicationForm form) {
        model.addAttribute("currentUser", user);

        SectionResource financeSection = sectionService.getFinanceSection(competitionId);
        boolean hasFinanceSection = financeSection != null;

        if(hasFinanceSection) {
            financeOverviewModelManager.addFinanceDetails(model, competitionId, applicationId);

            List<QuestionResource> costsQuestions = questionService.getQuestionsBySectionIdAndType(financeSection.getId(), QuestionType.COST);

            if(!form.isAdminMode()){
                String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
                financeHandler.getFinanceModelManager(organisationType).addOrganisationFinanceDetails(model, applicationId, costsQuestions, user.getId(), form);
            } else if(form.getImpersonateOrganisationId() != null){
                // find user in the organisation we want to impersonate.
                String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
                financeHandler.getFinanceModelManager(organisationType).addOrganisationFinanceDetails(model, applicationId, costsQuestions, user.getId(), form);
            }
        }
    }

    public Optional<OrganisationResource> getUserOrganisation(Long userId, List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
                .filter(uar -> uar.getUser().equals(userId))
                .map(uar -> organisationService.getOrganisationById(uar.getOrganisation()))
                .findFirst();
    }

    public void addApplicationInputs(ApplicationResource application, Model model) {
        model.addAttribute("application_title", application.getName());
        model.addAttribute("application_duration", String.valueOf(application.getDurationInMonths()));
        if(application.getStartDate() == null){
            model.addAttribute("application_startdate_day", "");
            model.addAttribute("application_startdate_month", "");
            model.addAttribute("application_startdate_year", "");
        }
        else{
            model.addAttribute("application_startdate_day", String.valueOf(application.getStartDate().getDayOfMonth()));
            model.addAttribute("application_startdate_month", String.valueOf(application.getStartDate().getMonthValue()));
            model.addAttribute("application_startdate_year", String.valueOf(application.getStartDate().getYear()));
        }
    }

}
