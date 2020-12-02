package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.application.forms.questions.team.form.HeukarPartnerOrganisationForm;
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
    public String showAddNewPartnerOrganisationForm(@ModelAttribute(value = "form", binding = false) HeukarPartnerOrganisationForm form,
                                                    BindingResult bindingResult,
                                                    Model model,
                                                    @PathVariable long applicationId,
                                                    @PathVariable long questionId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        ApplicationTeamAddOrganisationTypeViewModel populate =
                applicationTeamAddHeukarPartnerOrganisationPopulator.populate(application, questionId);

        model.addAttribute("model", populate);
        model.addAttribute("form", new HeukarPartnerOrganisationForm());
        return "application/questions/application-team-heukar-partner-organisation";
    }

    @GetMapping("/{existingId}")
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationCompositeId', 'ADD_NEW_ORGANISATION')")
    public String showEditPartnerOrganisationForm(@ModelAttribute(value = "form", binding = false) HeukarPartnerOrganisationForm form,
                                                  BindingResult bindingResult,
                                                  Model model,
                                                  @PathVariable long applicationId,
                                                  @PathVariable long questionId,
                                                  @PathVariable long existingId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        ApplicationTeamAddOrganisationTypeViewModel populate = applicationTeamAddHeukarPartnerOrganisationPopulator.populate(application, questionId);
        HeukarPartnerOrganisationResource existingPartner = heukarPartnerOrganisationRestService.getExistingPartnerById(existingId).getSuccess();
        HeukarPartnerOrganisationForm heukarPartnerOrganisationForm = new HeukarPartnerOrganisationForm();
        heukarPartnerOrganisationForm.setOrganisationTypeId(existingPartner.getOrganisationTypeResource().getId());
        heukarPartnerOrganisationForm.setId(existingPartner.getId());
        model.addAttribute("model", populate);
        model.addAttribute("form", heukarPartnerOrganisationForm);
        return "application/questions/application-team-heukar-partner-organisation";
    }

    @PostMapping
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationCompositeId', 'ADD_NEW_ORGANISATION')")
    public String submitForm(@Valid @ModelAttribute(value = "form") HeukarPartnerOrganisationForm form,
                             BindingResult bindingResult,
                             ValidationHandler validationHandler,
                             Model model,
                             @PathVariable long applicationId,
                             @PathVariable long questionId,
                             @RequestParam Long existingId) {
        boolean isAnEdit = existingId != null;
        if (isAnEdit) {
            heukarPartnerOrganisationRestService.updateHeukarOrgType(form.getId(), form.getOrganisationTypeId());
        } else {
            heukarPartnerOrganisationRestService.addNewHeukarOrgType(applicationId, form.getOrganisationTypeId());
        }
        Supplier<String> failureView = () -> showAddNewPartnerOrganisationForm(form, bindingResult, model, applicationId, questionId);
        return validationHandler.failNowOrSucceedWith(failureView,
                () -> redirectToApplicationTeam(applicationId, questionId));
    }

    private String redirectToApplicationTeam(long applicationId, long questionId) {
        return String.format("redirect:/application/%d/form/question/%d/team", applicationId, questionId);
    }


}
