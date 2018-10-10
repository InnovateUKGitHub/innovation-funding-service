package org.innovateuk.ifs.application.forms.yourfunding.controller;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.yourfunding.form.YourFundingForm;
import org.innovateuk.ifs.application.forms.yourfunding.populator.YourFundingFormPopulator;
import org.innovateuk.ifs.application.forms.yourfunding.populator.YourFundingViewModelPopulator;
import org.innovateuk.ifs.application.forms.yourfunding.saver.YourFundingSaver;
import org.innovateuk.ifs.application.forms.yourfunding.viewmodel.YourFundingViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-funding/{sectionId}")
public class YourFundingController {
    private static final String VIEW = "application/your-funding";

    @Autowired
    private YourFundingFormPopulator formPopulator;

    @Autowired
    private YourFundingViewModelPopulator viewModelPopulator;

    @Autowired
    private YourFundingSaver saver;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private ApplicantRestService applicantRestService;

    @GetMapping
    public String viewYourFunding(Model model,
                                  UserResource user,
                                  @PathVariable long applicationId,
                                  @PathVariable long sectionId,
                                  @ModelAttribute("form") YourFundingForm form) {

        YourFundingViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, user);
        model.addAttribute("model", viewModel);
        if (viewModel.isFundingSectionLocked()) {
            return VIEW;
        }
        formPopulator.populateForm(form, applicationId, user);
        return VIEW;
    }

    @PostMapping
    public String saveYourFunding(Model model,
                                  UserResource user,
                                  @PathVariable long applicationId,
                                  @PathVariable long sectionId,
                                  @Valid @ModelAttribute("form") YourFundingForm form,
                                  BindingResult bindingResult,
                                  ValidationHandler validationHandler) {

        saver.save(applicationId, form, user);
        return viewYourFunding(model, user, applicationId, sectionId, form);
    }

    @PostMapping(params = "edit")
    public String edit(Model model,
                       UserResource user,
                       @PathVariable long applicationId,
                       @PathVariable long sectionId,
                       @ModelAttribute("form") YourFundingForm form) {
        ApplicantSectionResource section = applicantRestService.getSection(user.getId(), applicationId, sectionId);
        sectionStatusRestService.markAsInComplete(sectionId, applicationId, section.getCurrentApplicant().getProcessRole().getId());
        return viewYourFunding(model, user, applicationId, sectionId, form);
    }

    @PostMapping(params = "complete")
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long sectionId,
                           @Valid @ModelAttribute("form") YourFundingForm form) {
        saver.save(applicationId, form, user);
        ApplicantSectionResource section = applicantRestService.getSection(user.getId(), applicationId, sectionId);
        sectionStatusRestService.markAsComplete(sectionId, applicationId, section.getCurrentApplicant().getProcessRole().getId());
        return viewYourFunding(model, user, applicationId, sectionId, form);
    }

    @PostMapping(params = "add_other_funding")
    public String addFundingRow(Model model,
                                  UserResource user,
                                  @PathVariable long applicationId,
                                  @PathVariable long sectionId,
                                  @ModelAttribute("form") YourFundingForm form) {

        YourFundingViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, user);
        model.addAttribute("model", viewModel);
        saver.addOtherFundingRow(form, applicationId, user);
        return VIEW;
    }

    @PostMapping(params = "remove_other_funding")
    public String removeFundingRow(Model model,
                                UserResource user,
                                @PathVariable long applicationId,

                                   @PathVariable long sectionId,
                                @ModelAttribute("form") YourFundingForm form,
                                @RequestParam("remove_other_funding") Long costId) {

        YourFundingViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, user);
        model.addAttribute("model", viewModel);
        saver.removeOtherFundingRow(form, costId);
        return VIEW;

    }
}
