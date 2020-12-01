package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.application.forms.questions.team.form.ApplicationTeamOrganisationTypeForm;
import org.innovateuk.ifs.application.forms.questions.team.populator.ApplicationTeamAddHeukarPartnerOrganisationPopulator;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamAddOrganisationTypeViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.heukar.service.HeukarPartnerOrganisationRestService;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
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
                                      @PathVariable long questionId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        ApplicationTeamAddOrganisationTypeViewModel populate =
                applicationTeamAddHeukarPartnerOrganisationPopulator.populate(application, questionId);

        model.addAttribute("model", populate);
        model.addAttribute("organisationForm", new ApplicationTeamOrganisationTypeForm());
        return "application/questions/application-team-heukar-partner-organisation";
    }

    @GetMapping("/{selectedId}")
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationCompositeId', 'ADD_NEW_ORGANISATION')")
    public String editPartnerOrganisationForm(@ModelAttribute(value = "form", binding = false) ApplicationTeamOrganisationTypeForm form,
                                              BindingResult bindingResult,
                                              Model model,
                                              @PathVariable long applicationId,
                                              @PathVariable long questionId,
                                              @PathVariable long selectedId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        ApplicationTeamAddOrganisationTypeViewModel populate = applicationTeamAddHeukarPartnerOrganisationPopulator.populate(application, questionId);
        HeukarPartnerOrganisationResource existingPartner = heukarPartnerOrganisationRestService.getExistingPartnerById(selectedId).getSuccess();
        ApplicationTeamOrganisationTypeForm applicationTeamOrganisationTypeForm = new ApplicationTeamOrganisationTypeForm();
        applicationTeamOrganisationTypeForm.setOrganisationTypeId(existingPartner.getOrganisationTypeResource().getId());
        model.addAttribute("model", populate);
        model.addAttribute("organisationForm", applicationTeamOrganisationTypeForm);
        return "application/questions/application-team-heukar-partner-organisation";
    }

    @PostMapping
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationCompositeId', 'ADD_NEW_ORGANISATION')")
    public String submitFormWithAdd(@Valid @ModelAttribute(value = "form") ApplicationTeamOrganisationTypeForm form,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    Model model,
                                    @PathVariable long applicationId,
                                    @PathVariable long questionId) {
        heukarPartnerOrganisationRestService.addNewHeukarOrgType(applicationId, form.getOrganisationTypeId());
        Supplier<String> failureView = () -> addOrganisationForm(form, bindingResult, model, applicationId, questionId);
        return validationHandler.failNowOrSucceedWith(failureView,
                () -> redirectToApplicationTeam(applicationId, questionId));
    }

    private String redirectToApplicationTeam(long applicationId, long questionId) {
        return String.format("redirect:/application/%d/form/question/%d/team", applicationId, questionId);
    }


}
