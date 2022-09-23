package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.transactional.ApplicationEoiEvidenceResponseService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.transactional.CompetitionEoiEvidenceConfigService;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.controller.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.file.service.FileService;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 **/
@RestController
@RequestMapping("/application")
public class ApplicationEoiEvidenceResponseController {

    @Autowired
    private ApplicationEoiEvidenceResponseService applicationEoiEvidenceResponseService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionEoiEvidenceConfigService competitionEoiEvidenceConfigService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    @Value("${ifs.data.service.file.storage.forminputresponse.max.filesize.bytes}")
    private Long maxFilesizeBytesForEoiEvidenceResponse;

    private final FileControllerUtils fileControllerUtils = new FileControllerUtils();


    @PostMapping(value = "/{applicationId}/eoi-evidence/{organisationId}/upload", produces = "application/json")
    public RestResult<ApplicationEoiEvidenceResponseResource> uploadEoiEvidence(@RequestHeader(value = "Content-Type", required = false) String contentType,
                                                                                @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                                                                @PathVariable("applicationId") long applicationId,
                                                                                @PathVariable("organisationId") long organisationId,
                                                                                @RequestParam(value = "filename", required = false) String originalFilename,
                                                                                UserResource userResource,
                                                                                HttpServletRequest request) {
        long competitionId = applicationService.getApplicationById(applicationId).getSuccess().getCompetition();
        long eoiEvidenceConfigId = competitionService.getCompetitionById(competitionId).getSuccess().getCompetitionEoiEvidenceConfigResource().getId();
        List<Long> validFileTypesIdsForEoiEvidence = competitionEoiEvidenceConfigService.getValidFileTypesIdsForEoiEvidence(eoiEvidenceConfigId).getSuccess();

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename,
                fileValidator, getMediaMimeTypes(validFileTypesIdsForEoiEvidence), maxFilesizeBytesForEoiEvidenceResponse, request,
                (fileAttributes, inputStreamSupplier) ->
                        applicationEoiEvidenceResponseService.upload(applicationId, organisationId, userResource, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }


    @DeleteMapping("/{applicationId}/eoi-evidence-response/delete/{userId}")
    public RestResult<ApplicationEoiEvidenceResponseResource> remove(@PathVariable("applicationId") long applicationId,
                                   @PathVariable("userId") long userId) {

          Optional<ApplicationEoiEvidenceResponseResource> applicationEoiEvidenceResponseResource =
                applicationEoiEvidenceResponseService.findOneByApplicationId(applicationId).getSuccess();
          UserResource userResource = baseUserService.getUserById(userId).getSuccess();
          if(applicationEoiEvidenceResponseResource.isPresent()) {
              return applicationEoiEvidenceResponseService.remove(applicationEoiEvidenceResponseResource.get(),  userResource).toGetResponse();
          }
          //TODO need to make this as void
          return RestResult.restSuccess(new ApplicationEoiEvidenceResponseResource());
    }

    @PostMapping("/{applicationId}/eoi-evidence-response/submit/{userId}")
    public RestResult<Void> submitEoiEvidence(@PathVariable("applicationId") long applicationId,
                                              @PathVariable("userId") long userId) {

        Optional<ApplicationEoiEvidenceResponseResource> applicationEoiEvidenceResponseResource =
                applicationEoiEvidenceResponseService.findOneByApplicationId(applicationId).getSuccess();
        UserResource userResource = baseUserService.getUserById(userId).getSuccess();
        if(applicationEoiEvidenceResponseResource.isPresent()) {
            return applicationEoiEvidenceResponseService.submit(applicationEoiEvidenceResponseResource.get(),  userResource).toGetResponse();
        }
        return RestResult.restSuccess();
    }


    @GetMapping ("/{applicationId}/eoi-evidence-response")
    public RestResult <Optional<ApplicationEoiEvidenceResponseResource>> findOneByApplicationId(@PathVariable("applicationId") long applicationId) {
        return applicationEoiEvidenceResponseService.findOneByApplicationId(applicationId).toGetResponse();
    }
    public ServiceResult<UserResource> getUserById(long id) {
        return find(userRepository.findById(id), notFoundError(UserResource.class, id)).andOnSuccessReturn(userMapper::mapToResource);
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
