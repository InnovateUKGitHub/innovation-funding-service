package org.innovateuk.ifs.application.forms.yourprojectcosts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.forms.saver.ApplicationSectionFinanceSaver;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.LabourForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.populator.ApplicationYourProjectCostsFormPopulator;
import org.innovateuk.ifs.application.forms.yourprojectcosts.populator.YourProjectCostsViewModelPopulator;
import org.innovateuk.ifs.application.forms.yourprojectcosts.saver.AbstractYourProjectCostsSaver;
import org.innovateuk.ifs.application.forms.yourprojectcosts.saver.ApplicationYourProjectCostsSaver;
import org.innovateuk.ifs.application.forms.yourprojectcosts.saver.YourProjectCostsAutosaver;
import org.innovateuk.ifs.application.forms.yourprojectcosts.validator.YourProjectCostsFormValidator;
import org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.origin.ApplicationSummaryOrigin;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "YOUR_PROJECT_COSTS_APPLICANT", description = "Applicants can all fill out the Your project costs section of the application.")
public class YourProjectCostsController extends AsyncAdaptor {
    private final static Logger LOG = LoggerFactory.getLogger(AbstractYourProjectCostsSaver.class);

    private static final String VIEW = "application/your-project-costs";

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
    private ApplicationSectionFinanceSaver completeSectionAction;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_COSTS", description = "Applicants and internal users can view the Your project costs page")
    public String viewYourProjectCosts(Model model,
                                       UserResource user,
                                       @PathVariable long applicationId,
                                       @PathVariable long organisationId,
                                       @PathVariable long sectionId,
                                       @RequestParam(value = "origin", required = false) String origin,
                                       @RequestParam MultiValueMap<String, String> queryParams) {
        String originQuery = "";
        if (origin != null) {
            originQuery = buildOriginQueryString(ApplicationSummaryOrigin.valueOf(origin), queryParams);
        }
        YourProjectCostsForm form = formPopulator.populateForm(applicationId, organisationId);
        model.addAttribute("form", form);
        return viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId, originQuery);
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
        Supplier<String> failureView = () -> viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId, "");
        validator.validate(form, validationHandler);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(form, applicationId, user));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(markAsComplete(sectionId, applicationId, user));
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
        return viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId, "");
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
        return viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId, "");
    }

    @PostMapping(params = "uploadOverheadFile")
    public String uploadOverheadSpreadsheet(Model model,
                                            UserResource user,
                                            @PathVariable long applicationId,
                                            @PathVariable long organisationId,
                                            @PathVariable long sectionId,
                                            @ModelAttribute("form") YourProjectCostsForm form,
                                            BindingResult bindingResult,
                                            ValidationHandler validationHandler) throws IOException {

        MultipartFile file = form.getOverhead().getFile();
        RestResult<FileEntryResource> fileEntryResult = overheadFileRestService.updateOverheadCalculationFile(form.getOverhead().getCostId(), file.getContentType(), file.getSize(), file.getOriginalFilename(), file.getBytes());
        if (fileEntryResult.isFailure()) {
            fileEntryResult.getErrors().forEach(error -> {
                if (UNSUPPORTED_MEDIA_TYPE.name().equals(error.getErrorKey())) {
                    bindingResult.rejectValue("overhead.file", "validation.finance.overhead.file.type");
                } else {
                    bindingResult.rejectValue("overhead.file", error.getErrorKey(), error.getArguments().toArray(), "");
                }
            });
        } else {
            form.getOverhead().setFilename(fileEntryResult.getSuccess().getName());
        }
        return viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId, "");
    }

    @PostMapping(params = "removeOverheadFile")
    public String removeOverheadSpreadsheet(Model model,
                                            UserResource user,
                                            @PathVariable long applicationId,
                                            @PathVariable long organisationId,
                                            @PathVariable long sectionId,
                                            @ModelAttribute("form") YourProjectCostsForm form) {
        overheadFileRestService.removeOverheadCalculationFile(form.getOverhead().getCostId()).getSuccess();
        return viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId, "");
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
            LabourCost cost = row.toCost();
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
            FinanceRowItem cost = row.toCost();
            row.setTotal(cost.getTotal());
        });
    }

    private String redirectToYourFinances(long applicationId) {
        return String.format("redirect:/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }

    private String viewYourProjectCosts(YourProjectCostsForm form, UserResource user, Model model, long applicationId, long sectionId, long organisationId, String originQuery) {
        recalculateTotals(form);
        YourProjectCostsViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, organisationId, user.isInternalUser(), originQuery);
        model.addAttribute("model", viewModel);
        return VIEW;
    }

    private long getProcessRoleId(long applicationId, long userId) {
        return getProcessRole(applicationId, userId).getId();
    }

    private ProcessRoleResource getProcessRole(long applicationId, long userId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccess();
    }

    private ValidationMessages markAsComplete(long sectionId, long applicationId, UserResource user) {
        ValidationMessages messages = new ValidationMessages();
        ProcessRoleResource role = getProcessRole(applicationId, user.getId());
        sectionStatusRestService.markAsComplete(sectionId, applicationId, role.getId()).getSuccess().forEach(messages::addAll);
        if (!messages.hasErrors()) {
            completeSectionAction.handleMarkProjectCostsAsComplete(role);
        }
        return messages;
    }
}
