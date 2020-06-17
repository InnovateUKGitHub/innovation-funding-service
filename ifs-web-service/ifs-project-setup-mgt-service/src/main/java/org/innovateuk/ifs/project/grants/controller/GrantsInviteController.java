package org.innovateuk.ifs.project.grants.controller;

import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.grants.service.GrantsInviteRestService;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole;
import org.innovateuk.ifs.project.grants.form.GrantsSendInviteForm;
import org.innovateuk.ifs.project.grants.viewmodel.GrantsInviteSendViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

@Controller
@RequestMapping("/project/{projectId}/grants/invite")
@SecuredBySpring(value = "TODO", description = "TODO")
@PreAuthorize("hasAnyAuthority('project_finance')")
public class GrantsInviteController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private GrantsInviteRestService grantsInviteRestService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @GetMapping("/send")
    public String inviteForm(Model model, @PathVariable long projectId, @ModelAttribute("form") GrantsSendInviteForm form) {
        List<PartnerOrganisationResource> organisations = partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess();
        model.addAttribute("model", new GrantsInviteSendViewModel(projectRestService.getProjectById(projectId).getSuccess(), organisations));
        return "project/grants-invite/invite";
    }

    @PostMapping("/send")
    public String sendInvite(Model model, @PathVariable long projectId, @Valid @ModelAttribute("form") GrantsSendInviteForm form,
                             BindingResult bindingResult, ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> inviteForm(model, projectId, form);
        Supplier<String> successView = () -> String.format("redirect:/project/%d/grants/invite", projectId);

        validateOrganisationNumber(form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            GrantsInviteResource resource = constructResource(projectId, form);
            validationHandler.addAnyErrors(grantsInviteRestService.invite(projectId, resource));
            return validationHandler.failNowOrSucceedWith(failureView, successView);
        });

    }

    private GrantsInviteResource constructResource(long projectId, GrantsSendInviteForm form) {
        Long organisationId = form.getOrganisationId();
        if (form.getRole() == GrantsInviteRole.GRANTS_PROJECT_MANAGER) {
            List<PartnerOrganisationResource> organisations = partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess();
            organisationId = organisations.stream()
                    .filter(PartnerOrganisationResource::isLeadOrganisation)
                    .findFirst()
                    .map(PartnerOrganisationResource::getOrganisation)
                    .orElseThrow(() -> new IFSRuntimeException("Uknown lead organisation"));
        }
        return new GrantsInviteResource(organisationId, form.getFirstName() + " " + form.getLastName(), form.getEmail(), form.getRole());
    }

    private void validateOrganisationNumber(GrantsSendInviteForm form, BindingResult bindingResult) {
        if (form.getRole() == GrantsInviteRole.GRANTS_PROJECT_FINANCE_CONTACT) {
            if (form.getOrganisationId() == null) {
                bindingResult.rejectValue("organisationId", "validation.grants.invite.organisation.required");
            }
        }
    }
}
