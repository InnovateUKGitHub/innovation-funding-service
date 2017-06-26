package org.innovateuk.ifs.application.forms.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.StringMultipartFileEditor;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * This controller will handle all download requests that are related to the application form.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
@PreAuthorize("hasAnyAuthority('applicant', 'comp_admin', 'project_finance')")
public class ApplicationDownloadController {

    private static final Log LOG = LogFactory.getLog(ApplicationDownloadController.class);

    @Autowired
    private FinanceService financeService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder, WebRequest webRequest) {
        dataBinder.registerCustomEditor(String.class, new StringMultipartFileEditor());
    }

    @GetMapping(QUESTION_URL + "{" + QUESTION_ID + "}/forminput/{formInputId}/download")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadApplicationFinanceFile(
            @PathVariable(APPLICATION_ID) final Long applicationId,
            @PathVariable("formInputId") final Long formInputId,
            UserResource user) {
        ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), applicationId);
        final ByteArrayResource resource = formInputResponseRestService.getFile(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        final FormInputResponseFileEntryResource fileDetails = formInputResponseRestService.getFileDetails(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileDetails.getFileEntryResource());
    }

    @GetMapping("/{applicationFinanceId}/finance-download")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadApplicationFinanceFile(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId) {

        final ByteArrayResource resource = financeService.getFinanceDocumentByApplicationFinance(applicationFinanceId).getSuccessObjectOrThrowException();
        final FileEntryResource fileDetails = financeService.getFinanceEntryByApplicationFinanceId(applicationFinanceId).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileDetails);
    }
}
