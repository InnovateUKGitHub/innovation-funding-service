package org.innovateuk.ifs.application.forms.sections.yourfeccosts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.application.forms.academiccosts.saver.AcademicCostSaver;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourFinancesViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourProjectFinancesViewModel;
import org.innovateuk.ifs.application.forms.sections.yourfeccosts.form.YourFECModelForm;
import org.innovateuk.ifs.application.forms.sections.yourfeccosts.form.YourFECModelFormPopulator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.defaultConverters;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.mappingErrorKeyToField;

/**
 * The Controller for the "Your FEC Costs" page in the Application Form process.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-fec-model/organisation/{organisationId}/section/{sectionId}")
public class YourFECCostsController {

    private static final String VIEW_PAGE = "application/sections/your-fec-model/your-fec-model";
    @Autowired
    private CommonYourFinancesViewModelPopulator commonViewModelPopulator;
    @Autowired
    private YourFECModelFormPopulator formPopulator;
    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private ProcessRoleRestService processRoleRestService;


    @GetMapping
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'stakeholder', 'external_finance', 'knowledge_transfer_adviser', 'supporter', 'assessor')")
    @SecuredBySpring(value = "VIEW_FEC_COSTS", description = "Applicants, stakeholders, internal users and kta can view the Your FEC costs page")
    public String viewPage(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser,
            Model model,
            @ModelAttribute("form") YourFECModelForm form) {
        formPopulator.populate(form, applicationId, organisationId);
        CommonYourProjectFinancesViewModel commonViewModelRequest =
                getViewModel(applicationId, sectionId, organisationId, loggedInUser);

        model.addAttribute("model", commonViewModelRequest);
        return VIEW_PAGE;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPDATE_FEC_COSTS", description = "Applicants can update their fec model")
    public String update(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @ModelAttribute YourFECModelForm form) {

        updateFECModelEnabled(applicationId, organisationId, form);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping("/auto-save")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPDATE_FEC_COSTS", description = "Applicants can update their fec model")
    public @ResponseBody JsonNode autosave(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @ModelAttribute YourFECModelForm form) {

        update(applicationId, organisationId, form);
        return new ObjectMapper().createObjectNode();
    }

    @PostMapping(params = "mark-as-complete")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_FEC_COSTS_AS_COMPLETE", description = "Applicants can mark their fec model as complete")
    public String markAsComplete(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser,
            @Valid @ModelAttribute("form") YourFECModelForm form,
            BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model) {

        Supplier<String> successView = () -> redirectToYourFinances(applicationId);
        Supplier<String> failureView = () ->  viewPage(applicationId, organisationId,sectionId,loggedInUser,model,form);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            updateFECModelEnabled(applicationId, organisationId, form);
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(
                        sectionService.markAsComplete(sectionId, applicationId, getProcessRole(applicationId, loggedInUser.getId()).getId()),
                        mappingErrorKeyToField("validation.application.fec.upload.required", "fecCertificateFile"), defaultConverters());
                return validationHandler.failNowOrSucceedWith(failureView, successView);
            });
        });
    }

    @PostMapping(params = "mark-as-incomplete")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_FEC_COSTS_AS_INCOMPLETE", description = "Applicants can mark the ir fec model as incomplete")
    public String markAsIncomplete(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser) {

        ProcessRoleResource processRole = processRoleRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
        sectionService.markAsInComplete(sectionId, applicationId, processRole.getId());
        return redirectToViewPage(applicationId, organisationId, sectionId);
    }

    private void updateFECModelEnabled(long applicationId,
                                       long organisationId,
                                       YourFECModelForm form) {

        ApplicationFinanceResource finance =
                applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();

        finance.setFecModelEnabled(form.getFecModelEnabled());

        applicationFinanceRestService.update(finance.getId(), finance).getSuccess();
    }

    @PostMapping(params = "upload_fecCertificateFile")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPLOAD_FEC_CERTIFICATE", description = "Lead applicant can upload their fec certificate")
    public String uploadFECCertificateFile(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long organisationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") YourFECModelForm form,
                                BindingResult bindingResult) throws IOException {
        ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();
        MultipartFile fecCertificateFile = form.getFecCertificateFile();
        RestResult<FileEntryResource> result = applicationFinanceRestService.addFECCertificateFile(finance.getId(), fecCertificateFile.getContentType(),
                fecCertificateFile.getSize(), fecCertificateFile.getOriginalFilename(), fecCertificateFile.getBytes());
        if(result.isFailure()) {
            result.getErrors().forEach(error ->
                    bindingResult.rejectValue("fecCertificateFile", error.getErrorKey(), error.getArguments().toArray(), "")
            );
        } else {
               form.setFecCertificateFileName(result.getSuccess().getName());
        }

        model.addAttribute("model", getViewModel(applicationId, sectionId, organisationId, user));
        return VIEW_PAGE;
    }

    @PostMapping(params = "remove_fecCertificateFile")
    @AsyncMethod
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "REMOVE_FEC_CERTIFICATE", description = "Lead applicant can remove their fec certificate")
    public String removeFECCertificateFile(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long organisationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") YourFECModelForm form) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();
        applicationFinanceRestService.removeFECCertificateFile(finance.getId());
        return redirectToViewPage(applicationId, organisationId, sectionId);
    }

    private CommonYourProjectFinancesViewModel getViewModel(long applicationId, long sectionId, long organisationId, UserResource user) {
        return commonViewModelPopulator.populate(organisationId, applicationId, sectionId, user);
    }

    private String redirectToViewPage(long applicationId, long organisationId, long sectionId) {
        return "redirect:" + APPLICATION_BASE_URL +
                String.format("%d/form/your-fec-model/organisation/%d/section/%d",
                        applicationId,
                        organisationId,
                        sectionId);
    }

    private String redirectToYourFinances(long applicationId) {
        return "redirect:" + String.format("%s%d/form/FINANCE", APPLICATION_BASE_URL, applicationId);
    }

    private ProcessRoleResource getProcessRole(long applicationId, long userId) {
        return processRoleRestService.findProcessRole(userId, applicationId).getSuccess();
    }
}
