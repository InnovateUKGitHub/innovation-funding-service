package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.application.forms.questions.team.form.HeukarPartnerOrganisationForm;
import org.innovateuk.ifs.application.forms.questions.team.populator.ApplicationTeamHeukarPartnerOrganisationPopulator;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamHeukarPartnerOrganisationViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
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
@SecuredBySpring(value = "Controller", description = "Only applicants can edit their application team", securedType = ApplicationTeamController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamHeukarPartnerOrganisationController {

    private static final String ORGANISATION_TYPE_ID = "organisationTypeId";

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ApplicationTeamHeukarPartnerOrganisationPopulator applicationTeamHeukarPartnerOrganisationPopulator;

    @Autowired
    private HeukarPartnerOrganisationRestService heukarPartnerOrganisationRestService;

    @GetMapping
    public String showAddNewPartnerOrganisationForm(@ModelAttribute(value = "form", binding = false) HeukarPartnerOrganisationForm form,
                                                    BindingResult bindingResult,
                                                    Model model,
                                                    @PathVariable long applicationId,
                                                    @PathVariable long questionId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        ApplicationTeamHeukarPartnerOrganisationViewModel populate =
                applicationTeamHeukarPartnerOrganisationPopulator.populate(application, questionId);

        model.addAttribute("model", populate);
        return "application/questions/application-team-heukar-partner-organisation";
    }

    @GetMapping("/{existingId}")
    public String showEditPartnerOrganisationForm(@ModelAttribute(value = "form", binding = false) HeukarPartnerOrganisationForm form,
                                                  BindingResult bindingResult,
                                                  Model model,
                                                  @PathVariable long applicationId,
                                                  @PathVariable long questionId,
                                                  @PathVariable long existingId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        ApplicationTeamHeukarPartnerOrganisationViewModel populate = applicationTeamHeukarPartnerOrganisationPopulator.populate(application, questionId);

        HeukarPartnerOrganisationResource existingPartner = heukarPartnerOrganisationRestService.getExistingPartnerById(existingId).getSuccess();
        HeukarPartnerOrganisationForm heukarPartnerOrganisationForm = new HeukarPartnerOrganisationForm();
        heukarPartnerOrganisationForm.setOrganisationTypeId(existingPartner.getOrganisationTypeId());
        heukarPartnerOrganisationForm.setId(existingPartner.getId());

        model.addAttribute("model", populate);
        model.addAttribute("form", heukarPartnerOrganisationForm);
        return "application/questions/application-team-heukar-partner-organisation";
    }

    @PostMapping
    public String submitForm(@Valid @ModelAttribute(value = "form") HeukarPartnerOrganisationForm form,
                             BindingResult bindingResult,
                             ValidationHandler validationHandler,
                             Model model,
                             @PathVariable long applicationId,
                             @PathVariable long questionId,
                             @RequestParam Long existingId) {
        Supplier<String> failureView = () -> showAddNewPartnerOrganisationForm(form, bindingResult, model, applicationId, questionId);
        return validationHandler.failNowOrSucceedWith(failureView,
                () -> {
                    boolean isAnEdit = existingId != null;
                    if (isAnEdit) {
                        heukarPartnerOrganisationRestService.updateHeukarOrgType(existingId, form.getOrganisationTypeId());
                    } else {
                        heukarPartnerOrganisationRestService.addNewHeukarOrgType(applicationId, form.getOrganisationTypeId());
                    }
                    return redirectToApplicationTeam(applicationId, questionId);
                });
    }

    private String redirectToApplicationTeam(long applicationId, long questionId) {
        return String.format("redirect:/application/%d/form/question/%d/team", applicationId, questionId);
    }


}
