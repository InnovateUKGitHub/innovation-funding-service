package org.innovateuk.ifs.application.forms.sections.yourfunding.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingAmountForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingPercentageForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.populator.YourFundingFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourfunding.populator.YourFundingViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourfunding.saver.YourFundingSaver;
import org.innovateuk.ifs.application.forms.sections.yourfunding.validator.YourFundingFormValidator;
import org.innovateuk.ifs.application.forms.sections.yourfunding.viewmodel.YourFundingViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-funding/organisation/{organisationId}/section/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "YOUR_FUNDING_APPLICANT", description = "Applicants can all fill out the Your Funding section of the application.")
public class YourFundingController {
    private static final String VIEW = "application/sections/your-funding/your-funding";

    @Autowired
    private YourFundingFormPopulator formPopulator;

    @Autowired
    private YourFundingViewModelPopulator viewModelPopulator;

    @Autowired
    private YourFundingSaver saver;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private YourFundingFormValidator yourFundingFormValidator;

    @GetMapping
    @SecuredBySpring(value = "VIEW_YOUR_FUNDING_SECTION", description = "Internal users can access the sections in the 'Your project finances'")
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    public String managementViewYourFunding(@ModelAttribute("form") YourFundingPercentageForm bindingForm,
                                            Model model,
                                            UserResource user,
                                            @PathVariable long applicationId,
                                            @PathVariable long sectionId,
                                            @PathVariable long organisationId) {
        YourFundingViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, organisationId, user);
        model.addAttribute("model", viewModel);
        if (viewModel.isFundingSectionLocked()) {
            return VIEW;
        }
        AbstractYourFundingForm form = formPopulator.populateForm(applicationId, organisationId);
        model.addAttribute("form", form);
        return VIEW;
    }

    @PostMapping(params = "grantClaimPercentage")
    public String saveYourFunding(Model model,
                                  UserResource user,
                                  @PathVariable long applicationId,
                                  @PathVariable long sectionId,
                                  @PathVariable long organisationId,
                                  @ModelAttribute("form") YourFundingPercentageForm form) {
        saver.save(applicationId, form, organisationId);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(params = "amount")
    public String saveYourFunding(Model model,
                                  UserResource user,
                                  @PathVariable long applicationId,
                                  @PathVariable long sectionId,
                                  @PathVariable long organisationId,
                                  @ModelAttribute("form") YourFundingAmountForm form) {
        saver.save(applicationId, form, organisationId);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(params = {"complete", "grantClaimPercentage"})
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long sectionId,
                           @PathVariable long organisationId,
                           @ModelAttribute("form") YourFundingPercentageForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        return complete(model,
                user,
                applicationId,
                sectionId,
                organisationId,
                form,
                bindingResult,
                validationHandler,
                f -> saver.save(applicationId, f, organisationId));
    }


    @PostMapping(params = {"complete", "amount"})
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long sectionId,
                           @PathVariable long organisationId,
                           @ModelAttribute("form") YourFundingAmountForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        return complete(model,
                user,
                applicationId,
                sectionId,
                organisationId,
                form,
                bindingResult,
                validationHandler,
                f -> saver.save(applicationId, f, organisationId));
    }

    private <FormType extends AbstractYourFundingForm> String complete(Model model,
                                                                       UserResource user,
                                                                       @PathVariable long applicationId,
                                                                       @PathVariable long sectionId,
                                                                       @PathVariable long organisationId,
                                                                       @ModelAttribute("form") FormType form,
                                                                       BindingResult bindingResult,
                                                                       ValidationHandler validationHandler,
                                                                       Function<FormType, ServiceResult<Void>> saveFunction) {
        Supplier<String> successView = () -> redirectToYourFinances(applicationId);
        Supplier<String> failureView = () -> viewYourFunding(model, applicationId, sectionId, organisationId, user);
        yourFundingFormValidator.validate(form, bindingResult, user, applicationId);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saveFunction.apply(form));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(sectionStatusRestService.markAsComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId()))
                        .getSuccess());
                return validationHandler.failNowOrSucceedWith(failureView, successView);
            });
        });
    }

    @PostMapping(params = "edit")
    public String edit(UserResource user,
                       @PathVariable long applicationId,
                       @PathVariable long sectionId,
                       @PathVariable long organisationId) {
        sectionStatusRestService.markAsInComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId())).getSuccess();
        return format("redirect:/application/%d/form/your-funding/organisation/%d/section/%d", applicationId, organisationId, sectionId);
    }

    @PostMapping(params = "add_cost")
    public String addFundingRowFormPost(Model model,
                                        UserResource user,
                                        @PathVariable long applicationId,
                                        @PathVariable long sectionId,
                                        @PathVariable long organisationId,
                                        @ModelAttribute("form") YourFundingPercentageForm form) {

        saver.addOtherFundingRow(form);
        return viewYourFunding(model, applicationId, sectionId, organisationId, user);
    }

    @PostMapping(params = "remove_cost")
    public String removeFundingRowFormPost(Model model,
                                           UserResource user,
                                           @PathVariable long applicationId,
                                           @PathVariable long sectionId,
                                           @PathVariable long organisationId,
                                           @ModelAttribute("form") YourFundingPercentageForm form,
                                           @RequestParam("remove_cost") String costId) {

        saver.removeOtherFundingRowForm(form, costId);
        return viewYourFunding(model, applicationId, sectionId, organisationId, user);
    }

    @PostMapping("auto-save")
    public @ResponseBody
    JsonNode ajaxAutoSave(UserResource user,
                          @PathVariable long applicationId,
                          @RequestParam String field,
                          @RequestParam String value) {
        Optional<Long> fieldId = saver.autoSave(field, value, applicationId, user);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        fieldId.ifPresent(id -> node.put("fieldId", id));
        return node;
    }

    @PostMapping("remove-row/{rowId}")
    public @ResponseBody
    JsonNode ajaxRemoveRow(UserResource user,
                           @PathVariable long applicationId,
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

    private String redirectToYourFinances(long applicationId) {
        return String.format("redirect:/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }

    private String viewYourFunding(Model model, long applicationId, long sectionId, long organisationId, UserResource user) {
        YourFundingViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, organisationId, user);
        model.addAttribute("model", viewModel);
        return VIEW;
    }

    private long getProcessRoleId(long applicationId, long userId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccess().getId();
    }
}
