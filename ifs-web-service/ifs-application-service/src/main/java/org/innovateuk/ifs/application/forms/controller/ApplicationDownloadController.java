package org.innovateuk.ifs.application.forms.controller;


import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.StringMultipartFileEditor;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * This controller will handle all download requests that are related to the application form.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
@SecuredBySpring(value="Controller", description = "ApplicationDownloadController")
@PreAuthorize("hasAnyAuthority('applicant', 'comp_admin', 'project_finance', 'assessor', 'monitoring_officer')")
public class ApplicationDownloadController {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder, WebRequest webRequest) {
        dataBinder.registerCustomEditor(String.class, new StringMultipartFileEditor());
    }

    @GetMapping(QUESTION_URL + "{" + QUESTION_ID + "}/forminput/{formInputId}/file/{fileEntryId}/download")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadApplicationFinanceFile(
            @PathVariable(APPLICATION_ID) final Long applicationId,
            @PathVariable("formInputId") final Long formInputId,
            @PathVariable("fileEntryId") final Long fileEntryId,
            UserResource user) {
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(applicationId).getSuccess();
        ProcessRoleResource processRole = processRoles.stream()
                .filter(role -> user.getId().equals(role.getUser()))
                .findAny()
                .orElseGet(() -> leadRoleIfUserIsMonitoringOfficer(processRoles, user));
        final ByteArrayResource resource = formInputResponseRestService.getFile(formInputId, applicationId, processRole.getId(), fileEntryId).getSuccess();
        final FormInputResponseFileEntryResource fileDetails = formInputResponseRestService.getFileDetails(formInputId, applicationId, processRole.getId(), fileEntryId).getSuccess();
        return getFileResponseEntity(resource, fileDetails.getFileEntryResource());
    }

    private ProcessRoleResource leadRoleIfUserIsMonitoringOfficer(List<ProcessRoleResource> processRoles, UserResource user) {
        if (user.hasRole(Role.MONITORING_OFFICER)) {
                return processRoles.stream()
                        .filter(pr -> pr.getRole().equals(Role.LEADAPPLICANT))
                        .findFirst()
                        .orElseThrow(this::roleNotFound);
        } else {
            throw roleNotFound();
        }
    }
    private ObjectNotFoundException roleNotFound() {
        return new ObjectNotFoundException("No process role found for user", Collections.emptyList());
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
