package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseController;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationPrintPopulator;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.application.service.AssessorFeedbackRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.toField;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * Handles the Competition Management Application overview page (and associated actions).
 */
@Controller
@RequestMapping("/competition/{competitionId}/application")
@PreAuthorize("hasAnyAuthority('applicant', 'project_finance', 'comp_admin')")
public class CompetitionManagementApplicationController extends BaseController {

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private AssessorFeedbackRestService assessorFeedbackRestService;

    @Autowired
    protected ProcessRoleService processRoleService;

    @Autowired
    protected ApplicationPrintPopulator applicationPrintPopulator;

    @Autowired
    private CompetitionManagementApplicationService competitionManagementApplicationService;

    @GetMapping("/{applicationId}")
    public String displayApplicationOverview(@PathVariable("applicationId") final Long applicationId,
                                             @PathVariable("competitionId") final Long competitionId,
                                             @ModelAttribute("form") ApplicationForm form,
                                             @RequestParam(value = "origin", defaultValue = "ALL_APPLICATIONS") String origin,
                                             @RequestParam MultiValueMap<String, String> queryParams,
                                             Model model,
                                             HttpServletRequest request
    ) {
        UserResource user = getLoggedUser(request);
        return competitionManagementApplicationService
                .validateApplicationAndCompetitionIds(applicationId, competitionId, (application) -> competitionManagementApplicationService
                        .displayApplicationOverview(user, applicationId, competitionId, form, origin, queryParams, model, application));
    }

    @GetMapping("/{applicationId}/assessorFeedback")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> downloadAssessorFeedbackFile(
            @PathVariable("applicationId") final Long applicationId) {

        final ByteArrayResource resource = assessorFeedbackRestService.getAssessorFeedbackFile(applicationId).getSuccessObjectOrThrowException();
        final FileEntryResource fileDetails = assessorFeedbackRestService.getAssessorFeedbackFileDetails(applicationId).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileDetails);
    }

    @PostMapping(value = "/{applicationId}", params = "uploadAssessorFeedback")
    public String uploadAssessorFeedbackFile(
            @PathVariable("competitionId") final Long competitionId,
            @PathVariable("applicationId") final Long applicationId,
            @RequestParam(value = "origin", defaultValue = "ALL_APPLICATIONS") String origin,
            @RequestParam MultiValueMap<String, String> queryParams,
            @ModelAttribute("form") ApplicationForm applicationForm,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            HttpServletRequest request) {

        Supplier<String> failureView = () -> displayApplicationOverview(applicationId, competitionId, applicationForm, origin, queryParams, model, request);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            MultipartFile file = applicationForm.getAssessorFeedback();

            RestResult<FileEntryResource> uploadFileResult = assessorFeedbackRestService.addAssessorFeedbackDocument(applicationId,
                    file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));

            return validationHandler.
                    addAnyErrors(uploadFileResult, toField("assessorFeedback")).
                    failNowOrSucceedWith(failureView, () -> redirectToApplicationOverview(competitionId, applicationId));
        });
    }

    @PostMapping(value = "/{applicationId}", params = "removeAssessorFeedback")
    public String removeAssessorFeedbackFile(@PathVariable("competitionId") final Long competitionId,
                                             @PathVariable("applicationId") final Long applicationId,
                                             @RequestParam(value = "origin", defaultValue = "ALL_APPLICATIONS") String origin,
                                             @RequestParam MultiValueMap<String, String> queryParams,
                                             Model model,
                                             @ModelAttribute("form") ApplicationForm applicationForm,
                                             @SuppressWarnings("unused") BindingResult bindingResult,
                                             ValidationHandler validationHandler,
                                             HttpServletRequest request) {

        Supplier<String> failureView = () -> displayApplicationOverview(applicationId, competitionId, applicationForm, origin, queryParams, model, request);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            RestResult<Void> removeFileResult = assessorFeedbackRestService.removeAssessorFeedbackDocument(applicationId);

            return validationHandler.
                    addAnyErrors(removeFileResult, toField("assessorFeedback")).
                    failNowOrSucceedWith(failureView, () -> redirectToApplicationOverview(competitionId, applicationId));
        });
    }

    @GetMapping("/{applicationId}/forminput/{formInputId}/download")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadQuestionFile(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("formInputId") final Long formInputId,
            HttpServletRequest request) throws ExecutionException, InterruptedException {
        final UserResource user = getLoggedUser(request);
        ProcessRoleResource processRole;
        if (user.hasRole(UserRoleType.COMP_ADMIN)) {
            long processRoleId = formInputResponseRestService.getByFormInputIdAndApplication(formInputId, applicationId).getSuccessObjectOrThrowException().get(0).getUpdatedBy();
            processRole = processRoleService.getById(processRoleId).get();
        } else {
            processRole = processRoleService.findProcessRole(user.getId(), applicationId);
        }

        final ByteArrayResource resource = formInputResponseRestService.getFile(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        final FormInputResponseFileEntryResource fileDetails = formInputResponseRestService.getFileDetails(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileDetails.getFileEntryResource());
    }


    /**
     * Printable version of the application
     */
    @GetMapping(value = "/{applicationId}/print")
    public String printManagementApplication(@PathVariable("applicationId") Long applicationId,
                                             @PathVariable("competitionId") Long competitionId,
                                             Model model, HttpServletRequest request) {
        return competitionManagementApplicationService
                .validateApplicationAndCompetitionIds(applicationId, competitionId, (application) -> applicationPrintPopulator.print(applicationId, model, request));
    }

    private String redirectToApplicationOverview(Long competitionId, Long applicationId) {
        return "redirect:/competition/" + competitionId + "/application/" + applicationId;
    }
}
