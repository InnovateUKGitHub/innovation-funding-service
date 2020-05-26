package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.forms.questions.team.form.ApplicationTeamAddressForm;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamAddressViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationOrganisationAddressRestService;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.address.resource.OrganisationAddressType.INTERNATIONAL;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/team/organisation/{organisationId}/address")
@SecuredBySpring(value = "Controller", description = "Only applicants can edit their application team", securedType = ApplicationTeamAddressController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamAddressController {

    @Autowired
    private ApplicationOrganisationAddressRestService applicationOrganisationAddressRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @GetMapping
    public String getAddress(@ModelAttribute(value = "form", binding = false) ApplicationTeamAddressForm form,
                             BindingResult bindingResult,
                             Model model,
                             @PathVariable long applicationId,
                             @PathVariable long questionId,
                             @PathVariable long organisationId) {
        AddressResource address = applicationOrganisationAddressRestService.getAddress(applicationId, organisationId, INTERNATIONAL).getSuccess();
        form.populate(address);
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        model.addAttribute("model", new ApplicationTeamAddressViewModel(application, organisation, questionId));
        return "application/questions/application-team-address";
    }

    @PostMapping
    public String updateAddress(@Valid @ModelAttribute(value = "form") ApplicationTeamAddressForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler,
                           Model model,
                            @PathVariable long applicationId,
                            @PathVariable long questionId,
                            @PathVariable long organisationId) {
        Supplier<String> failureView = () -> getAddress(form, bindingResult, model, applicationId, questionId, organisationId);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(applicationOrganisationAddressRestService.updateAddress(applicationId, organisationId, INTERNATIONAL, form.toAddress()));
            return validationHandler.failNowOrSucceedWith(failureView,
                    () -> redirectToApplicationTeam(applicationId, questionId));
        });
    }

    private String redirectToApplicationTeam(long applicationId, long questionId) {
        return String.format("redirect:/application/%d/form/question/%d/team", applicationId, questionId);
    }
}
