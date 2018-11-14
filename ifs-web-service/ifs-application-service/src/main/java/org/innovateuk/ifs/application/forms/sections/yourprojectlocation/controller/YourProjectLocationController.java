package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.controller;

import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form.YourProjectLocationForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form.YourProjectLocationFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.viewmodel.YourProjectLocationViewModel;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.viewmodel.YourProjectLocationViewModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/project-location/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "PROJECT_LOCATION_APPLICANT",
        description = "Applicants can all fill out the Project Location section of the application.")
public class YourProjectLocationController {

    private YourProjectLocationViewModelPopulator viewModelPopulator;
    private YourProjectLocationFormPopulator fomrPopulator;
    private UserRestService userRestService;

    YourProjectLocationController(
            YourProjectLocationViewModelPopulator viewModelPopulator,
            YourProjectLocationFormPopulator fomrPopulator,
            UserRestService userRestService) {

        this.viewModelPopulator = viewModelPopulator;
        this.fomrPopulator = fomrPopulator;
        this.userRestService = userRestService;
    }

    // TODO DW - parallelize?
    @GetMapping("/")
    public String view(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser,
            Model model) {

        long userId = loggedInUser.getId();
        ProcessRoleResource processRole = userRestService.findProcessRole(userId, applicationId).getSuccess();

        YourProjectLocationViewModel viewModel = viewModelPopulator.populate(userId, applicationId, sectionId);
        YourProjectLocationForm form = fomrPopulator.populate(applicationId, processRole.getOrganisationId());

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);

        return "application/your-project-location";
    }

}
