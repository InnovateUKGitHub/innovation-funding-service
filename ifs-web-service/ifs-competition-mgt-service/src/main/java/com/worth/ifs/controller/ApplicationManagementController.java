package com.worth.ifs.controller;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.resource.AppendixResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.AssessorFeedbackRestService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.rest.RestFailure;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.viewmodel.AssessorFeedbackViewModel;
import com.worth.ifs.exception.UnableToReadUploadedFile;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileEntryRestService;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.form.service.FormInputRestService;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.util.MessageUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.worth.ifs.competition.resource.CompetitionResource.Status.ASSESSOR_FEEDBACK;
import static com.worth.ifs.competition.resource.CompetitionResource.Status.FUNDERS_PANEL;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@Controller
@RequestMapping("/competition/{competitionId}/application")
public class ApplicationManagementController extends AbstractApplicationController {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(ApplicationManagementController.class);

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private AssessorFeedbackRestService assessorFeedbackRestService;

    @RequestMapping(value= "/{applicationId}", method = RequestMethod.GET)
    public String displayApplicationForCompetitionAdministrator(@PathVariable("applicationId") final Long applicationId,
                                                                @ModelAttribute("form") ApplicationForm form,
                                                                Model model,
                                                                HttpServletRequest request
    ){
        UserResource user = getLoggedUser(request);
        form.setAdminMode(true);

        List<FormInputResponseResource> responses = formInputResponseService.getByApplication(applicationId);

        ApplicationResource application = applicationService.getById(applicationId);
        // so the mode is viewonly
        application.enableViewMode();

        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSections(application, competition, user.getId(), Optional.empty(), Optional.empty(), model, form);
        addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form);
        addAppendices(applicationId, responses, model);

        model.addAttribute("applicationReadyForSubmit", false);
        model.addAttribute("isCompManagementDownload", true);

        AssessorFeedbackViewModel assessorFeedbackViewModel = getAssessorFeedbackViewModel(application, competition);
        model.addAttribute("assessorFeedback", assessorFeedbackViewModel);

        return "competition-mgt-application-overview";
    }

    @RequestMapping(value = "/{applicationId}/assessorFeedback", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadAssessorFeedbackFile(
            @PathVariable("applicationId") final Long applicationId) {

        final ByteArrayResource resource = assessorFeedbackRestService.getAssessorFeedbackFile(applicationId).getSuccessObjectOrThrowException();
        return getPdfFile(resource);
    }

    @RequestMapping(value = "/{applicationId}", params = "uploadAssessorFeedback", method = RequestMethod.POST)
    public  String uploadAssessorFeedbackFile(
            @PathVariable("applicationId") final Long applicationId,
            @ModelAttribute ApplicationForm applicationForm,
            Model model,
            HttpServletRequest request) {

        List<String> validationErrors = saveFileUpload(applicationId, request);

        return displayApplicationForCompetitionAdministrator(applicationId, applicationForm, model, request);
    }

    @RequestMapping(value = "/{applicationId}/forminput/{formInputId}/download", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadQuestionFile(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("formInputId") final Long formInputId,
            HttpServletRequest request) throws ExecutionException, InterruptedException {
        final UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ProcessRoleResource processRole;
        if(user.hasRole(UserRoleType.COMP_ADMIN)){
            long processRoleId = formInputResponseService.getByFormInputIdAndApplication(formInputId, applicationId).getSuccessObjectOrThrowException().get(0).getUpdatedBy();
            processRole = processRoleService.getById(processRoleId).get();
        } else {
            processRole = processRoleService.findProcessRole(user.getId(), applicationId);
        }

        final ByteArrayResource resource = formInputResponseService.getFile(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        return getPdfFile(resource);
    }

    private ResponseEntity<ByteArrayResource> getPdfFile(ByteArrayResource resource) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(resource.contentLength());
        httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
        return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
    }

    private void addAppendices(Long applicationId, List<FormInputResponseResource> responses, Model model) {
        final List<AppendixResource> appendices = responses.stream().filter(fir -> fir.getFileEntry() != null).
                map(fir -> {
                    FormInputResource formInputResource = formInputRestService.getById(fir.getFormInput()).getSuccessObject();
                    FileEntryResource fileEntryResource = fileEntryRestService.findOne(fir.getFileEntry()).getSuccessObject();
                    String title = formInputResource.getDescription() != null ? formInputResource.getDescription() : fileEntryResource.getName();
                    return new AppendixResource(applicationId, formInputResource.getId(), title, fileEntryResource);
                }).
                collect(Collectors.toList());
        model.addAttribute("appendices", appendices);
    }

    private AssessorFeedbackViewModel getAssessorFeedbackViewModel(ApplicationResource application, CompetitionResource competition) {

        boolean readonly = !asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK).contains(competition.getCompetitionStatus());

        Long assessorFeedbackFileEntry = application.getAssessorFeedbackFileEntry();

        if (assessorFeedbackFileEntry != null) {
            RestResult<FileEntryResource> fileEntry = assessorFeedbackRestService.getAssessorFeedbackFileDetails(application.getId());
            return AssessorFeedbackViewModel.withExistingFile(fileEntry.getSuccessObjectOrThrowException().getName(), readonly);
        } else {
            return AssessorFeedbackViewModel.withNoFile(readonly);
        }
    }

    private List<String> saveFileUpload(Long applicationId, HttpServletRequest request){

        RestResult<FileEntryResource> uploadResult = uploadFormInput(applicationId, request);

        return uploadResult.handleSuccessOrFailure(
                failure -> lookupValidationErrorsFromServiceFailures(request, failure),
                success -> emptyList()
        );
    }

    private List<String> lookupValidationErrorsFromServiceFailures(HttpServletRequest request, RestFailure failure) {
        return simpleMap(failure.getErrors(),
                e -> MessageUtil.getFromMessageBundle(messageSource, e.getErrorKey(), "Unknown error on file upload", request.getLocale()));
    }

    private RestResult<FileEntryResource> uploadFormInput(Long applicationId, HttpServletRequest request) {

        final Map<String, MultipartFile> fileMap = ((StandardMultipartHttpServletRequest) request).getFileMap();
        final MultipartFile file = fileMap.get("assessorFeedback");

        if (file != null && !file.isEmpty()) {

            try {

                return assessorFeedbackRestService.addAssessorFeedbackDocument(applicationId,
                        file.getContentType(),
                        file.getSize(),
                        file.getOriginalFilename(),
                        file.getBytes());

            } catch (IOException e) {
                LOG.error(e);
                throw new UnableToReadUploadedFile();
            }
        }

        LOG.error("No assessorFeedback file was available during upload within the multipart request");
        throw new UnableToReadUploadedFile();
    }
}
