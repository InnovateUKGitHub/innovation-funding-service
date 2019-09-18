package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.LabourForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator.ApplicationYourProjectCostsFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator.YourProjectCostsViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver.ApplicationYourProjectCostsSaver;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver.YourProjectCostsAutosaver;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver.YourProjectCostsCompleter;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.validator.YourProjectCostsFormValidator;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "YOUR_PROJECT_COSTS_APPLICANT", description = "Applicants can all fill out the Your project costs section of the application.")
public class YourProjectCostsController extends AsyncAdaptor {
    private static final String VIEW = "application/sections/your-project-costs/your-project-costs";

    @Autowired
    private ApplicationYourProjectCostsFormPopulator formPopulator;

    @Autowired
    private YourProjectCostsViewModelPopulator viewModelPopulator;

    @Autowired
    private ApplicationYourProjectCostsSaver saver;

    @Autowired
    private YourProjectCostsAutosaver autosaver;

    @Autowired
    private YourProjectCostsFormValidator validator;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private OverheadFileRestService overheadFileRestService;

    @Autowired
    private YourProjectCostsCompleter completeSectionAction;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_COSTS", description = "Applicants and internal users can view the Your project costs page")
    public String viewYourProjectCosts(Model model,
                                       UserResource user,
                                       @PathVariable long applicationId,
                                       @PathVariable long organisationId,
                                       @PathVariable long sectionId) {
        YourProjectCostsForm form = formPopulator.populateForm(applicationId, organisationId);
        model.addAttribute("form", form);
        return viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId);
    }

    @PostMapping
    @AsyncMethod
    public String saveYourProjectCosts(Model model,
                                       UserResource user,
                                       @PathVariable long applicationId,
                                       @PathVariable long sectionId,
                                       @ModelAttribute("form") YourProjectCostsForm form) {
        saver.save(form, applicationId, user);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(params = "complete")
    @AsyncMethod
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long organisationId,
                           @PathVariable long sectionId,
                           @Valid @ModelAttribute("form") YourProjectCostsForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        Supplier<String> successView = () -> redirectToYourFinances(applicationId);
        Supplier<String> failureView = () -> viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId);
        validator.validate(applicationId, form, validationHandler);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(form, applicationId, user));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(completeSectionAction.markAsComplete(sectionId, applicationId, getProcessRole(applicationId, user.getId())));
                return validationHandler.failNowOrSucceedWith(failureView, successView);
            });
        });
    }

    @PostMapping(params = "edit")
    public String edit(Model model,
                       UserResource user,
                       @PathVariable long applicationId,
                       @PathVariable long organisationId,
                       @PathVariable long sectionId,
                       @ModelAttribute("form") YourProjectCostsForm form) {
        sectionStatusRestService.markAsInComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId())).getSuccess();
        return String.format("redirect:/application/%d/form/your-project-costs/organisation/%d/section/%d", applicationId, organisationId, sectionId);
    }

    @PostMapping(params = "remove_cost")
    public String removeRowPost(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long organisationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") YourProjectCostsForm form,
                                @RequestParam("remove_cost") String removeId) {
        saver.removeRowFromForm(form, removeId);
        return viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId);
    }

    @PostMapping(params = "add_cost")
    public String addRowPost(Model model,
                             UserResource user,
                             @PathVariable long applicationId,
                             @PathVariable long organisationId,
                             @PathVariable long sectionId,
                             @ModelAttribute("form") YourProjectCostsForm form,
                             @RequestParam("add_cost") FinanceRowType rowType) throws InstantiationException, IllegalAccessException {

        saver.addRowForm(form, rowType);
        return viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId);
    }

    @PostMapping(params = "uploadOverheadFile")
    public String uploadOverheadSpreadsheet(Model model,
                                            UserResource user,
                                            @PathVariable long applicationId,
                                            @PathVariable long organisationId,
                                            @PathVariable long sectionId,
                                            @ModelAttribute("form") YourProjectCostsForm form,
                                            BindingResult bindingResult,
                                            ValidationHandler validationHandler) {
        Supplier<String> view = () -> viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId);
        MultipartFile file = form.getOverhead().getFile();
        return validationHandler.performFileUpload("overhead.file", view, () -> overheadFileRestService
                .updateOverheadCalculationFile(form.getOverhead().getCostId(), file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file))
                .andOnSuccessDo(result -> form.getOverhead().setFilename(result.getName())));
    }

    @PostMapping(params = "removeOverheadFile")
    public String removeOverheadSpreadsheet(Model model,
                                            UserResource user,
                                            @PathVariable long applicationId,
                                            @PathVariable long organisationId,
                                            @PathVariable long sectionId,
                                            @ModelAttribute("form") YourProjectCostsForm form) {
        overheadFileRestService.removeOverheadCalculationFile(form.getOverhead().getCostId()).getSuccess();
        return viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId);
    }

    @PostMapping("remove-row/{rowId}")
    public @ResponseBody
    JsonNode ajaxRemoveRow(UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable String rowId) {
        saver.removeFinanceRow(rowId);
        return new ObjectMapper().createObjectNode();
    }

    @PostMapping("add-row/{rowType}")
    public String ajaxAddRow(Model model,
                             @PathVariable FinanceRowType rowType) throws InstantiationException, IllegalAccessException {
        YourProjectCostsForm form = new YourProjectCostsForm();
        form.setLabour(new LabourForm());
        Map.Entry<String, AbstractCostRowForm> map = saver.addRowForm(form, rowType);

        model.addAttribute("form", form);
        model.addAttribute("id", map.getKey());
        model.addAttribute("row", map.getValue());
        return String.format("application/your-project-costs-fragments :: ajax_%s_row", rowType.name().toLowerCase());
    }

    @PostMapping("auto-save")
    public @ResponseBody
    JsonNode ajaxAutoSave(UserResource user,
                          @PathVariable long applicationId,
                          @RequestParam String field,
                          @RequestParam String value) {
        Optional<Long> fieldId = autosaver.autoSave(field, value, applicationId, user);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        fieldId.ifPresent(id -> node.put("fieldId", id));
        return node;
    }

    private void recalculateTotals(YourProjectCostsForm form) {
        form.getLabour().getRows().forEach((id, row) -> {
            LabourCost cost = row.toCost(null);
            row.setTotal(cost.getTotal(form.getLabour().getWorkingDaysPerYear()));
            row.setRate(cost.getRate(form.getLabour().getWorkingDaysPerYear()));
        });
        recalculateTotal(form.getMaterialRows());
        recalculateTotal(form.getCapitalUsageRows());
        recalculateTotal(form.getSubcontractingRows());
        recalculateTotal(form.getTravelRows());
        recalculateTotal(form.getOtherRows());
    }

    private void recalculateTotal(Map<String, ? extends AbstractCostRowForm> rows) {
        rows.forEach((id, row) -> {
            FinanceRowItem cost = row.toCost(null);
            row.setTotal(cost.getTotal());
        });
    }

    private String redirectToYourFinances(long applicationId) {
        return String.format("redirect:/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }

    private String viewYourProjectCosts(YourProjectCostsForm form, UserResource user, Model model, long applicationId, long sectionId, long organisationId) {
        recalculateTotals(form);
        YourProjectCostsViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, organisationId, user.isInternalUser());
        model.addAttribute("model", viewModel);
        return VIEW;
    }

    private long getProcessRoleId(long applicationId, long userId) {
        return getProcessRole(applicationId, userId).getId();
    }

    private ProcessRoleResource getProcessRole(long applicationId, long userId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccess();
    }

}
