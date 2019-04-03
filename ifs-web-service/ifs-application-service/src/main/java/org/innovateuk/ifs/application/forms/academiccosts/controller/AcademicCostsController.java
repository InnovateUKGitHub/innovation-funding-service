package org.innovateuk.ifs.application.forms.academiccosts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.application.forms.academiccosts.populator.AcademicCostFormPopulator;
import org.innovateuk.ifs.application.forms.academiccosts.populator.AcademicCostViewModelPopulator;
import org.innovateuk.ifs.application.forms.academiccosts.saver.AcademicCostSaver;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver.YourProjectCostsCompleter;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
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

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.defaultConverters;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.mappingErrorKeyToField;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/academic-costs/organisation/{organisationId}/section/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "ACADEMIC_COSTS_APPLICANT", description = "Applicants can all fill out the Your project costs section of the application.")
public class AcademicCostsController {

    private static final String VIEW = "application/academic-costs";

    @Autowired
    private AcademicCostFormPopulator formPopulator;

    @Autowired
    private AcademicCostViewModelPopulator viewModelPopulator;

    @Autowired
    private AcademicCostSaver saver;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private YourProjectCostsCompleter completeSectionAction;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_ACADEMIC_COSTS", description = "Applicants and internal users can view the academic project costs page")
    public String viewAcademicCosts(Model model,
                                    UserResource user,
                                    @PathVariable long applicationId,
                                    @PathVariable long organisationId,
                                    @PathVariable long sectionId,
                                    @ModelAttribute("form") AcademicCostForm form) {
        formPopulator.populate(form, applicationId, organisationId);
        model.addAttribute("model", viewModelPopulator.populate(organisationId, applicationId, sectionId, !user.isInternalUser()));
        return VIEW;
    }

    @PostMapping
    @AsyncMethod
    public String saveAcademicCosts(Model model,
                                    UserResource user,
                                    @PathVariable long applicationId,
                                    @PathVariable long organisationId,
                                    @PathVariable long sectionId,
                                    @ModelAttribute("form") AcademicCostForm form) {
        saver.save(form, applicationId, organisationId);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(params = "complete")
    @AsyncMethod
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long organisationId,
                           @PathVariable long sectionId,
                           @Valid @ModelAttribute("form") AcademicCostForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        Supplier<String> successView = () -> redirectToYourFinances(applicationId);
        Supplier<String> failureView = () -> viewAcademicCosts(model, user, applicationId, organisationId, sectionId, form);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(form, applicationId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(
                        completeSectionAction.markAsComplete(sectionId, applicationId, getProcessRole(applicationId, user.getId())),
                        mappingErrorKeyToField("validation.application.jes.upload.required", "jesFile"), defaultConverters());
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
                       @ModelAttribute("form") AcademicCostForm form) {
        sectionStatusRestService.markAsInComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId())).getSuccess();
        return redirectToAcademicCosts(applicationId, organisationId, sectionId);
    }

    @PostMapping(params = "remove_jes")
    @AsyncMethod
    public String removeJesFile(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long organisationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") AcademicCostForm form) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();
        applicationFinanceRestService.removeFinanceDocument(finance.getId());
        saver.save(form, applicationId, organisationId);
        return redirectToAcademicCosts(applicationId, organisationId, sectionId);
    }

    @PostMapping(params = "upload_jes")
    public String uploadJesFile(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long organisationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") AcademicCostForm form,
                                BindingResult bindingResult) throws IOException {
        ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();
        RestResult<FileEntryResource> result = applicationFinanceRestService.addFinanceDocument(finance.getId(), form.getJesFile().getContentType(), form.getJesFile().getSize(), form.getJesFile().getOriginalFilename(), form.getJesFile().getBytes());
        if(result.isFailure()) {
            result.getErrors().forEach(error ->
                    bindingResult.rejectValue("jesFile", error.getErrorKey(), error.getArguments().toArray(), "")
            );
        } else {
            form.setFilename(result.getSuccess().getName());
        }

        model.addAttribute("model", viewModelPopulator.populate(organisationId, applicationId, sectionId, true));
        return VIEW;
    }

    @PostMapping("auto-save")
    public @ResponseBody
    JsonNode ajaxAutoSave(@PathVariable long applicationId,
                          @PathVariable long organisationId,
                          @RequestParam String field,
                          @RequestParam String value) {
        Optional<Long> fieldId = saver.autoSave(field, value, applicationId, organisationId);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        fieldId.ifPresent(id -> node.put("fieldId", id));
        return node;
    }

    private String redirectToYourFinances(long applicationId) {
        return String.format("redirect:/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }

    private String redirectToAcademicCosts(long applicationId, long organisationId, long sectionId) {
        return String.format("redirect:/application/%s/form/academic-costs/organisation/%S/section/%s", applicationId, organisationId, sectionId);
    }

    private long getProcessRoleId(long applicationId, long userId) {
        return getProcessRole(applicationId, userId).getId();
    }

    private ProcessRoleResource getProcessRole(long applicationId, long userId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccess();
    }
}
