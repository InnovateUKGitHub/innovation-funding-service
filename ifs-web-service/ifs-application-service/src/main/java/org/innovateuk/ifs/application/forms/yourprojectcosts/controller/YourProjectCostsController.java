package org.innovateuk.ifs.application.forms.yourprojectcosts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.populator.YourProjectCostsFormPopulator;
import org.innovateuk.ifs.application.forms.yourprojectcosts.populator.YourProjectCostsViewModelPopulator;
import org.innovateuk.ifs.application.forms.yourprojectcosts.saver.YourProjectCostsAutosaver;
import org.innovateuk.ifs.application.forms.yourprojectcosts.saver.YourProjectCostsSaver;
import org.innovateuk.ifs.application.forms.yourprojectcosts.validator.YourProjectCostsFormValidator;
import org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "YOUR_PROJECT_COSTS_APPLICANT", description = "Applicants can all fill out the Your project costs section of the application.")
public class YourProjectCostsController {
    private final static Logger LOG = LoggerFactory.getLogger(YourProjectCostsSaver.class);

    private static final String VIEW = "application/your-project-costs";

    @Autowired
    private YourProjectCostsFormPopulator formPopulator;

    @Autowired
    private YourProjectCostsViewModelPopulator viewModelPopulator;

    @Autowired
    private YourProjectCostsSaver saver;

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


    @GetMapping("/{applicantOrganisationId}")
    @SecuredBySpring(value = "MANAGEMENT_VIEW_YOUR_FUNDING_SECTION", description = "Internal users can access the sections in the 'Your Finances'")
    @PreAuthorize("hasAnyAuthority('support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    public String managementViewYourProjectCosts(Model model,
                                            UserResource user,
                                            @PathVariable long applicationId,
                                            @PathVariable long sectionId,
                                            @PathVariable long applicantOrganisationId,
                                            @ModelAttribute("form") YourProjectCostsForm form,
                                            @RequestParam(value = "origin", defaultValue = "MANAGEMENT_DASHBOARD") String origin,
                                            @RequestParam MultiValueMap<String, String> queryParams) {

        String originQuery = buildOriginQueryString(ApplicationSummaryOrigin.valueOf(origin), queryParams);
        YourProjectCostsViewModel viewModel = viewModelPopulator.populateManagement(applicationId, sectionId, applicantOrganisationId, originQuery);
        model.addAttribute("model", viewModel);
        formPopulator.populateForm(form, applicationId, user, Optional.of(applicantOrganisationId));
        return VIEW;
    }

    @GetMapping
    public String viewYourProjectCosts(Model model,
                                       UserResource user,
                                       @PathVariable long applicationId,
                                       @PathVariable long sectionId,
                                       @ModelAttribute("form") YourProjectCostsForm form) {
        formPopulator.populateForm(form, applicationId, user, Optional.empty());
        return viewYourProjectCosts(form, model, applicationId, sectionId, user);
    }

    @PostMapping
    public String saveYourProjectCosts(Model model,
                                       UserResource user,
                                       @PathVariable long applicationId,
                                       @PathVariable long sectionId,
                                       @ModelAttribute("form") YourProjectCostsForm form) {
        saver.save(applicationId, form, user);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(params = "complete")
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long sectionId,
                           @Valid @ModelAttribute("form") YourProjectCostsForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        Supplier<String> successView = () -> redirectToYourFinances(applicationId);
        Supplier<String> failureView = () -> viewYourProjectCosts(form, model, applicationId, sectionId, user);
        validator.validate(form, validationHandler);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(applicationId, form, user));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                sectionStatusRestService.markAsComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId()))
                        .getSuccess().forEach(validationHandler::addAnyErrors);
                return validationHandler.failNowOrSucceedWith(failureView, successView);
            });
        });
    }

    @PostMapping(params = "edit")
    public String edit(Model model,
                       UserResource user,
                       @PathVariable long applicationId,
                       @PathVariable long sectionId,
                       @ModelAttribute("form") YourProjectCostsForm form) {
        sectionStatusRestService.markAsInComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId())).getSuccess();
        return viewYourProjectCosts(model, user, applicationId, sectionId, form);
    }

    @PostMapping(params = "remove_cost")
    public String removeRowPost(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") YourProjectCostsForm form,
                                @RequestParam("remove_row") List<String> removeRequest) {
        String id = removeRequest.get(0);
        FinanceRowType type = FinanceRowType.valueOf(removeRequest.get(1));
        saver.removeRowFromForm(form, type, id);
        return viewYourProjectCosts(form, model, applicationId, sectionId, user);

    }

    @PostMapping(params = "add_cost")
    public String addRowPost(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") YourProjectCostsForm form,
                                @RequestParam("add_cost") FinanceRowType rowType) throws InstantiationException, IllegalAccessException {

        saver.addRowForm(form, rowType, applicationId, user);
        return viewYourProjectCosts(form, model, applicationId, sectionId, user);
    }

    @PostMapping(params = "uploadOverheadFile")
    public String uploadOverheadSpreadsheet(Model model,
                             UserResource user,
                             @PathVariable long applicationId,
                             @PathVariable long sectionId,
                             @ModelAttribute("form") YourProjectCostsForm form,
                                            BindingResult bindingResult,
                                            ValidationHandler validationHandler) throws IOException {

        MultipartFile file = form.getOverhead().getFile();
        RestResult<FileEntryResource> fileEntryResult = overheadFileRestService.updateOverheadCalculationFile(form.getOverhead().getCostId(), file.getContentType(), file.getSize(), file.getOriginalFilename(), file.getBytes());
        if(fileEntryResult.isFailure()) {
            fileEntryResult.getErrors().forEach(error -> {
                if(UNSUPPORTED_MEDIA_TYPE.name().equals(error.getErrorKey())) {
                    bindingResult.rejectValue("overhead.file", "validation.finance.overhead.file.type");
                } else {
                    bindingResult.rejectValue("overhead.file", error.getErrorKey(), error.getArguments().toArray(), "");
                }
            });
        } else {
            form.getOverhead().setFilename(fileEntryResult.getSuccess().getName());
        }
        return viewYourProjectCosts(form, model, applicationId, sectionId, user);
    }

    @PostMapping(params = "removeOverheadFile")
    public String removeOverheadSpreadsheet(Model model,
                             UserResource user,
                             @PathVariable long applicationId,
                             @PathVariable long sectionId,
                             @ModelAttribute("form") YourProjectCostsForm form) {
        overheadFileRestService.removeOverheadCalculationFile(form.getOverhead().getCostId()).getSuccess();
        return viewYourProjectCosts(form, model, applicationId, sectionId, user);
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
                             UserResource user,
                             @PathVariable long applicationId,
                             @PathVariable FinanceRowType rowType) throws InstantiationException, IllegalAccessException {
        YourProjectCostsForm form = new YourProjectCostsForm();
        AbstractCostRowForm row = saver.addRowForm(form, rowType, applicationId, user);
        model.addAttribute("form", form);
        model.addAttribute("id", row.getCostId());
        model.addAttribute("row", row);
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
        form.getLabourCosts().forEach((id, row) -> {
            LabourCost cost = row.toCost();
            row.setTotal(cost.getTotal(form.getWorkingDaysPerYear()));
            row.setRate(cost.getRate(form.getWorkingDaysPerYear()));
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

    private String viewYourProjectCosts(YourProjectCostsForm form, Model model, long applicationId, long sectionId, UserResource user) {
        recalculateTotals(form);
        YourProjectCostsViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, user);
        model.addAttribute("model", viewModel);
        return VIEW;
    }

    private long getProcessRoleId(long applicationId, long userId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccess().getId();
    }
}
