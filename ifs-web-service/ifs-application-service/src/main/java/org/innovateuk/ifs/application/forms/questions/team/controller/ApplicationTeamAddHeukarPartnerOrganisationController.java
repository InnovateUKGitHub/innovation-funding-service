package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.application.forms.questions.team.form.ApplicationTeamOrganisationTypeForm;
import org.innovateuk.ifs.application.forms.questions.team.populator.ApplicationTeamAddHeukarPartnerOrganisationPopulator;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamAddOrganisationTypeViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.heukar.service.HeukarPartnerOrganisationRestService;
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
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/team/heukar-partner-org")
public class ApplicationTeamAddHeukarPartnerOrganisationController {

    private static final String ORGANISATION_TYPE_ID = "organisationTypeId";

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ApplicationTeamAddHeukarPartnerOrganisationPopulator applicationTeamAddHeukarPartnerOrganisationPopulator;

    @Autowired
    private HeukarPartnerOrganisationRestService heukarPartnerOrganisationRestService;

    @GetMapping
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationCompositeId', 'ADD_NEW_ORGANISATION')")
    public String addOrganisationForm(@ModelAttribute(value = "form", binding = false) ApplicationTeamOrganisationTypeForm form,
                                      BindingResult bindingResult,
                                      Model model,
                                      @PathVariable long applicationId,
                                      @PathVariable long questionId,
                                      @RequestParam(name = "selected", required = false) Long selected) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        if (selected != null) {
            //Then it's an eidt
        }
        ApplicationTeamAddOrganisationTypeViewModel populate =
                applicationTeamAddHeukarPartnerOrganisationPopulator.populate(application, questionId, selected);

        model.addAttribute("model", populate);
        model.addAttribute("organisationForm", new ApplicationTeamOrganisationTypeForm());
        return "application/questions/application-team-heukar-partner-organisation";
    }

    @PostMapping
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationCompositeId', 'ADD_NEW_ORGANISATION')")
    public String addOrganisation(@Valid @ModelAttribute(value = "form") ApplicationTeamOrganisationTypeForm form,
                                  BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model,
                                  @PathVariable long applicationId,
                                  @PathVariable long questionId) {
        heukarPartnerOrganisationRestService.addNewHeukarOrgType(applicationId, form.getOrganisationTypeId());
        Supplier<String> failureView = () -> addOrganisationForm(form, bindingResult, model, applicationId, questionId, null);
        return validationHandler.failNowOrSucceedWith(failureView,
                () -> redirectToApplicationTeam(applicationId, questionId));
    }

    private String redirectToApplicationTeam(long applicationId, long questionId) {
        return String.format("redirect:/application/%d/form/question/%d/team", applicationId, questionId);

    }
}
