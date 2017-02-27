package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.form.ApplicationTeamAddOrganisationForm;
import org.innovateuk.ifs.application.populator.ApplicationTeamAddOrganisationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * TODO
 */
@Controller
@RequestMapping("/application/{applicationId}/team")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamAddOrganisationController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private ApplicationTeamAddOrganisationModelPopulator applicationTeamAddOrganisationModelPopulator;

    @RequestMapping(value = "/addOrganisation", method = RequestMethod.GET)
    public String getAddOrganisation(Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamAddOrganisationForm form) {

        // TODO should check the logged in user is the lead applicant?

        return doViewAddOrganisation(model, applicationId);
    }

    @RequestMapping(value = "/addOrganisation", method = RequestMethod.POST)
    public String submitAddOrganisation(Model model,
                                        @PathVariable("applicationId") long applicationId,
                                        @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamAddOrganisationForm form,
                                        @SuppressWarnings("unused") BindingResult bindingResult,
                                        ValidationHandler validationHandler) {

        // TODO should check the logged in user is the lead applicant?

        ApplicationResource applicationResource = applicationService.getById(applicationId);

        Supplier<String> failureView = () -> getAddOrganisation(model, applicationId, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<InviteResultsResource> updateResult = inviteRestService.createInvitesByInviteOrganisation(
                    form.getOrganisationName(), createInvites(form, applicationResource.getId())).toServiceResult();
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> format("redirect:/application/%s/team", applicationResource.getId()));
        });
    }

    @RequestMapping(path = "/addOrganisation", params = {"addApplicant"}, method = RequestMethod.POST)
    public String addApplicant(Model model,
                               @PathVariable("applicationId") long applicationId,
                               @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamAddOrganisationForm form) {
        form.getApplicants().add(new ApplicantInviteForm());
        return doViewAddOrganisation(model, applicationId);
    }

    @RequestMapping(path = "/addOrganisation", params = {"removeApplicant"}, method = RequestMethod.POST)
    public String removeApplicant(Model model,
                                  @PathVariable("applicationId") long applicationId,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamAddOrganisationForm form,
                                  @RequestParam(name = "removeApplicant") Integer position) {
        form.getApplicants().remove(position.intValue());
        return doViewAddOrganisation(model, applicationId);
    }

    private String doViewAddOrganisation(Model model, long applicationId) {
        model.addAttribute("model", applicationTeamAddOrganisationModelPopulator.populateModel(applicationId));
        return "application-team/add-organisation";
    }

    private List<ApplicationInviteResource> createInvites(ApplicationTeamAddOrganisationForm applicationTeamAddOrganisationForm, long applicationId) {
        return applicationTeamAddOrganisationForm.getApplicants().stream()
                .map(applicantInviteForm -> createInvite(applicantInviteForm, applicationId)).collect(toList());
    }

    private ApplicationInviteResource createInvite(ApplicantInviteForm applicantInviteForm, long applicationId) {
        return new ApplicationInviteResource(applicantInviteForm.getName(), applicantInviteForm.getEmail(), applicationId);
    }
}