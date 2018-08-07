package org.innovateuk.ifs.application.forms.controller;


import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
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
@SecuredBySpring(value="Controller", description = "ApplicationDownloadController")
@PreAuthorize("hasAnyAuthority('applicant', 'comp_admin', 'project_finance', 'assessor')")
public class ApplicationDownloadController {

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

    @GetMapping(QUESTION_URL + "{" + QUESTION_ID + "}/forminput/{formInputId}/download/**")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadApplicationFinanceFile(
            @PathVariable(APPLICATION_ID) final Long applicationId,
            @PathVariable("formInputId") final Long formInputId,
            @PathVariable(value = "fileName", required = false) final String fileName,
            UserResource user) {
        ProcessRoleResource processRole = processRoleService.findProcessRolesByApplicationId(applicationId).stream()
                .filter(role -> user.getId().equals(role.getUser()))
                .findAny()
                .orElseThrow(ObjectNotFoundException::new);
        final ByteArrayResource resource = formInputResponseRestService.getFile(formInputId, applicationId, processRole.getId()).getSuccess();
        final FormInputResponseFileEntryResource fileDetails = formInputResponseRestService.getFileDetails(formInputId, applicationId, processRole.getId()).getSuccess();
        return getFileResponseEntity(resource, fileDetails.getFileEntryResource());
    }

    @GetMapping("/{applicationFinanceId}/finance-download")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadApplicationFinanceFile(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId) {

        final ByteArrayResource resource = financeService.getFinanceDocumentByApplicationFinance(applicationFinanceId).getSuccess();
        final FileEntryResource fileDetails = financeService.getFinanceEntryByApplicationFinanceId(applicationFinanceId).getSuccess();
        return getFileResponseEntity(resource, fileDetails);
    }
}
