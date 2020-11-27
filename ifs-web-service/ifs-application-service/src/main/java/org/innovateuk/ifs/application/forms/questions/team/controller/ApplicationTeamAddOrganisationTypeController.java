package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.application.forms.questions.team.form.ApplicationTeamOrganisationForm;
import org.innovateuk.ifs.application.forms.questions.team.form.ApplicationTeamOrganisationTypeForm;
import org.innovateuk.ifs.application.forms.questions.team.populator.ApplicationTeamAddOrganisationTypePopulator;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamAddOrganisationTypeViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/team/new-organisation-type")
public class ApplicationTeamAddOrganisationTypeController {

    private static final String ORGANISATION_TYPE_ID = "organisationTypeId";

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ApplicationTeamAddOrganisationTypePopulator applicationTeamAddOrganisationTypePopulator;

    @GetMapping
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationCompositeId', 'ADD_NEW_ORGANISATION')")
    public String addOrganisationForm(@ModelAttribute(value = "form", binding = false) ApplicationTeamOrganisationTypeForm form,
                                      BindingResult bindingResult,
                                      Model model,
                                      @PathVariable long applicationId,
                                      @PathVariable long questionId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        ApplicationTeamAddOrganisationTypeViewModel populate =
                applicationTeamAddOrganisationTypePopulator.populate(application, questionId);

        model.addAttribute("model", populate);
        model.addAttribute("organisationForm", new ApplicationTeamOrganisationTypeForm());
        return "application/questions/application-team-organisation-type";
    }

    @PostMapping
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationCompositeId', 'ADD_NEW_ORGANISATION')")
    public String addOrganisation(@Valid @ModelAttribute(value = "form") ApplicationTeamOrganisationTypeForm form,
                                  BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model,
                                  @PathVariable long applicationId,
                                  @PathVariable long questionId) {
        Supplier<String> failureView = () -> addOrganisationForm(form, bindingResult, model, applicationId, questionId);
        return validationHandler.failNowOrSucceedWith(failureView,
                () -> redirectToApplicationTeam(applicationId, questionId));
    }

    private String redirectToApplicationTeam(long applicationId, long questionId) {
        return String.format("redirect:/application/%d/form/question/%d/team", applicationId, questionId);
    }
}
