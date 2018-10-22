package org.innovateuk.ifs.application.forms.yourfunding.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.forms.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.yourfunding.form.YourFundingForm;
import org.innovateuk.ifs.application.forms.yourfunding.populator.YourFundingFormPopulator;
import org.innovateuk.ifs.application.forms.yourfunding.populator.YourFundingViewModelPopulator;
import org.innovateuk.ifs.application.forms.yourfunding.saver.YourFundingSaver;
import org.innovateuk.ifs.application.forms.yourfunding.viewmodel.YourFundingViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
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

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-funding/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "YOUR_FUNDING_APPLICANT", description = "Applicants can all fill out the Your Funding section of the application.")
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
    private UserRestService userRestService;

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
                                  @ModelAttribute("form") YourFundingForm form) {
        saver.save(applicationId, form, user);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(params = "edit")
    public String edit(Model model,
                       UserResource user,
                       @PathVariable long applicationId,
                       @PathVariable long sectionId,
                       @ModelAttribute("form") YourFundingForm form) {
        sectionStatusRestService.markAsInComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId())).getSuccess();
        return viewYourFunding(model, user, applicationId, sectionId, form);
    }

    @PostMapping(params = "complete")
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long sectionId,
                           @Valid @ModelAttribute("form") YourFundingForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        Supplier<String> successView = () -> redirectToYourFinances(applicationId);
        Supplier<String> failureView = () -> viewYourFunding(model, applicationId, sectionId, user);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(applicationId, form, user));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                sectionStatusRestService.markAsComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId()))
                        .getSuccess().forEach(validationHandler::addAnyErrors);
                return validationHandler.failNowOrSucceedWith(failureView, successView);
            });
        });
    }

    @PostMapping(params = "add_other_funding")
    public String addFundingRowFormPost(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") YourFundingForm form) {

        saver.addOtherFundingRow(form, applicationId, user);
        return viewYourFunding(model, applicationId, sectionId, user);
    }

    @PostMapping(params = "remove_other_funding")
    public String removeFundingRowFormPost(Model model,
                                   UserResource user,
                                   @PathVariable long applicationId,
                                   @PathVariable long sectionId,
                                   @ModelAttribute("form") YourFundingForm form,
                                   @RequestParam("remove_other_funding") String costId) {

        saver.removeOtherFundingRowForm(form, costId);
        return viewYourFunding(model, applicationId, sectionId, user);
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
    public String ajaxAddRow(Model model,
                         UserResource user,
                        @PathVariable long applicationId,
                        @ModelAttribute("form") YourFundingForm form,
                         BindingResult bindingResult) {
        form.setOtherFundingRows(new LinkedHashMap<>());
        saver.addOtherFundingRow(form, applicationId, user);
        OtherFundingRowForm row = form.getOtherFundingRows().entrySet().iterator().next().getValue();
        model.addAttribute("id", row.getCostId());
        model.addAttribute("row", row.getCostId());
        return "application/your-funding-fragments :: ajax_other_funding_row";
    }

    private String redirectToYourFinances(long applicationId) {
        return String.format("redirect:/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }

    private String viewYourFunding(Model model, long applicationId, long sectionId, UserResource user) {
        YourFundingViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, user);
        model.addAttribute("model", viewModel);
        return VIEW;
    }

    private long getProcessRoleId(long applicationId, long userId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccess().getId();
    }
}
