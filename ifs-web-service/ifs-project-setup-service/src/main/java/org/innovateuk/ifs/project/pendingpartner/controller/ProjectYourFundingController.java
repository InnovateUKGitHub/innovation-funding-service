package org.innovateuk.ifs.project.pendingpartner.controller;

import static java.lang.String.format;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingAmountForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingPercentageForm;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.pendingpartner.populator.ProjectYourFundingFormPopulator;
import org.innovateuk.ifs.project.pendingpartner.populator.ProjectYourFundingViewModelPopulator;
import org.innovateuk.ifs.project.pendingpartner.saver.ProjectYourFundingSaver;
import org.innovateuk.ifs.project.pendingpartner.validator.ProjectYourFundingFormValidator;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectYourFundingViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.status.controller.SetupStatusController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/your-funding")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = SetupStatusController.class)
@PreAuthorize("hasAnyAuthority('applicant')")
public class ProjectYourFundingController {
    private static final String VIEW = "project/pending-partner-progress/your-funding";

    @Autowired
    private ProjectYourFundingFormPopulator formPopulator;

    @Autowired
    private ProjectYourFundingViewModelPopulator viewModelPopulator;

    @Autowired
    private ProjectYourFundingSaver saver;

    @Autowired
    private ProjectYourFundingFormValidator projectYourFundingFormValidator;

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @GetMapping
    @SecuredBySpring(value = "VIEW_YOUR_FUNDING_SECTION", description = "Internal users can access the sections in the 'Your project finances'")
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    public String viewYourFunding(@ModelAttribute("form") YourFundingPercentageForm bindingForm,
                                  Model model,
                                  @PathVariable long projectId,
                                  @PathVariable long organisationId) {
        ProjectYourFundingViewModel viewModel = viewModelPopulator.populate(projectId, organisationId);
        model.addAttribute("model", viewModel);
        if (viewModel.isFundingSectionLocked()) {
            return VIEW;
        }
        AbstractYourFundingForm form = formPopulator.populateForm(projectId, organisationId);
        model.addAttribute("form", form);
        return VIEW;
    }

    @PostMapping(params = "grantClaimPercentage")
    public String saveYourFunding(@PathVariable long projectId,
                                  @PathVariable long organisationId,
                                  @ModelAttribute("form") YourFundingPercentageForm form) {
        saver.save(projectId, organisationId, form);
        return redirectToLandingPage(projectId, organisationId);
    }

    @PostMapping(params = "amount")
    public String saveYourFunding(@PathVariable long projectId,
                                  @PathVariable long organisationId,
                                  @ModelAttribute("form") YourFundingAmountForm form) {
        saver.save(projectId, organisationId, form);
        return redirectToLandingPage(projectId, organisationId);
    }

    @PostMapping(params = {"complete", "grantClaimPercentage"})
    public String complete(Model model,
                           @PathVariable long projectId,
                           @PathVariable long organisationId,
                           @ModelAttribute("form") YourFundingPercentageForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        return complete(model,
                projectId,
                organisationId,
                form,
                bindingResult,
                validationHandler,
                f -> saver.save(projectId, organisationId, f));
    }


    @PostMapping(params = {"complete", "amount"})
    public String complete(Model model,
                           @PathVariable long projectId,
                           @PathVariable long organisationId,
                           @ModelAttribute("form") YourFundingAmountForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        return complete(model,
                projectId,
                organisationId,
                form,
                bindingResult,
                validationHandler,
                f -> saver.save(projectId, organisationId, f));
    }

    private <FormType extends AbstractYourFundingForm> String complete(Model model,
                                                                       @PathVariable long projectId,
                                                                       @PathVariable long organisationId,
                                                                       @ModelAttribute("form") FormType form,
                                                                       BindingResult bindingResult,
                                                                       ValidationHandler validationHandler,
                                                                       Function<FormType, ServiceResult<Void>> saveFunction) {
        Supplier<String> successView = () -> redirectToLandingPage(projectId, organisationId);
        Supplier<String> failureView = () -> viewYourFunding(model, projectId, organisationId);
        projectYourFundingFormValidator.validate(form, bindingResult, projectId, organisationId);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saveFunction.apply(form));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(pendingPartnerProgressRestService.markYourFundingComplete(projectId, organisationId));
                return validationHandler.failNowOrSucceedWith(failureView, successView);
            });
        });
    }

    @PostMapping(params = "edit")
    public String edit(UserResource user,
                       @PathVariable long projectId,
                       @PathVariable long organisationId) {
        pendingPartnerProgressRestService.markYourFundingIncomplete(projectId, organisationId).getSuccess();
        return redirectToViewPage(projectId, organisationId);
    }

    @PostMapping(params = {"add_cost", "grantClaimPercentage"})
    public String addFundingRowFormPostPercentage(Model model,
                                        @PathVariable long projectId,
                                        @PathVariable long organisationId,
                                        @ModelAttribute("form") YourFundingPercentageForm form) {

        saver.addOtherFundingRow(form);
        return viewYourFunding(model, projectId, organisationId);
    }
    @PostMapping(params = {"add_cost", "amount"})
    public String addFundingRowFormPostAmount(Model model,
                                        @PathVariable long projectId,
                                        @PathVariable long organisationId,
                                        @ModelAttribute("form") YourFundingAmountForm form) {

        saver.addOtherFundingRow(form);
        return viewYourFunding(model, projectId, organisationId);
    }

    @PostMapping(params = {"remove_cost", "grantClaimPercentage"})
    public String removeFundingRowFormPostPercentage(Model model,
                                           @PathVariable long projectId,
                                           @PathVariable long organisationId,
                                           @ModelAttribute("form") YourFundingPercentageForm form,
                                           @RequestParam("remove_cost") String costId) {

        saver.removeOtherFundingRowForm(form, costId);
        return viewYourFunding(model, projectId, organisationId);
    }

    @PostMapping(params = {"remove_cost", "amount"})
    public String removeFundingRowFormPostAmount(Model model,
                                           @PathVariable long projectId,
                                           @PathVariable long organisationId,
                                           @ModelAttribute("form") YourFundingAmountForm form,
                                           @RequestParam("remove_cost") String costId) {

        saver.removeOtherFundingRowForm(form, costId);
        return viewYourFunding(model, projectId, organisationId);
    }

    @PostMapping("remove-row/{rowId}")
    public @ResponseBody
    JsonNode ajaxRemoveRow(UserResource user,
                           @PathVariable long projectId,
                           @PathVariable String rowId) {
        saver.removeOtherFundingRow(rowId);
        return new ObjectMapper().createObjectNode();
    }

    @PostMapping("add-row")
    public String ajaxAddRow(Model model) {
        YourFundingPercentageForm form = new YourFundingPercentageForm();
        form.setOtherFundingRows(new LinkedHashMap<>());
        saver.addOtherFundingRow(form);
        Map.Entry<String, OtherFundingRowForm> row = form.getOtherFundingRows().entrySet().iterator().next();
        model.addAttribute("form", form);
        model.addAttribute("id", row.getKey());
        model.addAttribute("row", row.getValue());
        return "application/your-funding-fragments :: ajax_other_funding_row";
    }


    private String redirectToViewPage(long projectId, long organisationId) {
        return format("redirect:/project/%d/organisation/%d/your-funding",
                projectId,
                organisationId);
    }

    private String redirectToLandingPage(long projectId, long organisationId) {
        return format("redirect:/project/%d/organisation/%d/pending-partner-progress",
                projectId,
                organisationId);
    }

    private String viewYourFunding(Model model, long projectId, long organisationId) {
        ProjectYourFundingViewModel viewModel = viewModelPopulator.populate(projectId, organisationId);
        model.addAttribute("model", viewModel);
        return VIEW;
    }
}
