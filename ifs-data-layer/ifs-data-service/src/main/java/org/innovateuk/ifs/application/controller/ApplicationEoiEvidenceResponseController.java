package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.application.transactional.ApplicationEoiEvidenceResponseService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.transactional.CompetitionEoiEvidenceConfigService;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.controller.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.file.resource.FileTypeCategory.*;

/**
 **/
@RestController
@RequestMapping("/application")
public class ApplicationEoiEvidenceResponseController {

    @Autowired
    private ApplicationEoiEvidenceResponseService applicationEoiEvidenceResponseService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionEoiEvidenceConfigService competitionEoiEvidenceConfigService;

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    @Value("${ifs.data.service.file.storage.applicationEoiEvidenceResponse.max.filesize.bytes}")
    private Long maxFilesizeBytesForApplicationEoiEvidenceResponse;

    private final FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @PostMapping(value = "/{applicationId}/eoi-evidence-response/{organisationId}/{userId}/upload", produces = "application/json")
    public RestResult<ApplicationEoiEvidenceResponseResource> uploadEoiEvidence(@RequestHeader(value = "Content-Type", required = false) String contentType,
                                                                                @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                                                                @PathVariable("applicationId") long applicationId,
                                                                                @PathVariable("organisationId") long organisationId,
                                                                                @PathVariable("userId") long userId,
                                                                                @RequestParam(value = "filename", required = false) String originalFilename,
                                                                                HttpServletRequest request) {
        long competitionId = applicationService.getApplicationById(applicationId).getSuccess().getCompetition();
        long eoiEvidenceConfigId = competitionService.getCompetitionById(competitionId).getSuccess().getCompetitionEoiEvidenceConfigResource().getId();
        List<Long> validFileTypesIdsForEoiEvidence = competitionEoiEvidenceConfigService.getValidFileTypesIdsForEoiEvidence(eoiEvidenceConfigId).getSuccess();
        UserResource userResource = baseUserService.getUserById(userId).getSuccess();

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename,
                fileValidator, getMediaMimeTypes(validFileTypesIdsForEoiEvidence), maxFilesizeBytesForApplicationEoiEvidenceResponse, request,
                (fileAttributes, inputStreamSupplier) ->
                        applicationEoiEvidenceResponseService.upload(applicationId, organisationId, userResource, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @PostMapping("/{applicationId}/eoi-evidence-response/remove/{userId}")
    public RestResult<ApplicationEoiEvidenceResponseResource> remove(@PathVariable("applicationId") long applicationId,
                                   @PathVariable("userId") long userId) {

          Optional<ApplicationEoiEvidenceResponseResource> applicationEoiEvidenceResponseResource =
                applicationEoiEvidenceResponseService.findOneByApplicationId(applicationId).getSuccess();
          UserResource userResource = baseUserService.getUserById(userId).getSuccess();
          if(applicationEoiEvidenceResponseResource.isPresent()) {
              ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseRes =  applicationEoiEvidenceResponseService.remove(applicationEoiEvidenceResponseResource.get(), userResource).getSuccess();
              return RestResult.restSuccess(applicationEoiEvidenceResponseRes);
          }

        return RestResult.restSuccess(new ApplicationEoiEvidenceResponseResource());
    }

    @PostMapping(value = "/{applicationId}/eoi-evidence-response/submit/{userId}", produces = "application/json")
    public RestResult<Void> submitEoiEvidence(@PathVariable("applicationId") long applicationId,
                                              @PathVariable("userId") long userId) {

        Optional<ApplicationEoiEvidenceResponseResource> applicationEoiEvidenceResponseResource =
                applicationEoiEvidenceResponseService.findOneByApplicationId(applicationId).getSuccess();
        UserResource userResource = baseUserService.getUserById(userId).getSuccess();
        if(applicationEoiEvidenceResponseResource.isPresent()) {
            return applicationEoiEvidenceResponseService.submit(applicationEoiEvidenceResponseResource.get(),  userResource).toPostResponse();
        }
        return RestResult.restSuccess();
    }

    @GetMapping ("/{applicationId}/view-eoi-evidence-file")
    public @ResponseBody
    ResponseEntity<Object> getEvidenceByApplication(
            @PathVariable("applicationId") long applicationId) throws IOException {
        return fileControllerUtils.handleFileDownload(() -> applicationEoiEvidenceResponseService.getEvidenceFileContents(applicationId));
    }

    @GetMapping ("/{applicationId}/view-eoi-evidence-file/details")
    public RestResult<FileEntryResource> getEvidenceDetailsByApplication(@PathVariable("applicationId") long applicationId) throws IOException {
        return applicationEoiEvidenceResponseService.getEvidenceFileEntryDetails(applicationId).toGetResponse();
    }

    @GetMapping ("/{applicationId}/eoi-evidence-response")
    public RestResult<Optional<ApplicationEoiEvidenceResponseResource>> findOneByApplicationId(@PathVariable("applicationId") long applicationId) {
        return applicationEoiEvidenceResponseService.findOneByApplicationId(applicationId).toGetResponse();
    }

    @GetMapping ("/{applicationId}/eoi-evidence-response-process-state")
    public RestResult<Optional<ApplicationEoiEvidenceState>> getApplicationEoiEvidenceState(@PathVariable("applicationId") long applicationId) {
        return applicationEoiEvidenceResponseService.getApplicationEoiEvidenceState(applicationId).toGetResponse();
    }

    private List<String> getMediaMimeTypes(List<Long> fileTypeIds) {
        List<String> validMediaTypes = new ArrayList<>();

        for (Long fileTypeId : fileTypeIds) {
            switch (fileTypeRepository.findById(fileTypeId).get().getName()) {
                case "PDF":
                    validMediaTypes.addAll(PDF.getMimeTypes());
                    break;
                case "Spreadsheets":
                    validMediaTypes.addAll(SPREADSHEET.getMimeTypes());
                    break;
                case "Text":
                    validMediaTypes.addAll(DOCUMENT.getMimeTypes());
                    break;
                default:
                    // do nothing
            }
        }
        return validMediaTypes;
    }
}
