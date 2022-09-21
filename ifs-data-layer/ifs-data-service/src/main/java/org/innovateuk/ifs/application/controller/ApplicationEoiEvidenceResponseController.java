package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.transactional.ApplicationEoiEvidenceResponseService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionEoiDocumentRepository;
import org.innovateuk.ifs.competition.transactional.CompetitionEoiEvidenceConfigService;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.controller.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.service.FileService;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
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
    private CompetitionEoiDocumentRepository competitionEoiDocumentRepository;

    @Autowired
    private CompetitionEoiEvidenceConfigService competitionEoiEvidenceConfigService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

//    @Value("${ifs.data.service.file.storage.interview.response.valid.media.types}")
//    private List<String> validMediaTypesForEoiEvidence;

    @Value("${ifs.data.service.file.storage.forminputresponse.max.filesize.bytes}")
    private Long maxFilesizeBytesForEoiEvidenceResponse;

    private final FileControllerUtils fileControllerUtils = new FileControllerUtils();


    @PostMapping(value = "/{applicationId}/eoi-evidence/{organisationId}/{userId}/upload", produces = "application/json")
    public RestResult<ApplicationEoiEvidenceResponseResource> uploadEoiEvidence(@RequestHeader(value = "Content-Type", required = false) String contentType,
                                                                                @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                                                                @PathVariable("applicationId") long applicationId,
                                                                                @PathVariable("organisationId") long organisationId,
                                                                                @PathVariable("userId") long userId,
                                                                                @RequestParam(value = "filename", required = false) String originalFilename,
                                                                                HttpServletRequest request) {
        long competitionId = applicationService.getApplicationById(applicationId).getSuccess().getCompetition();
        long eoiEvidenceConfigId = competitionService.getCompetitionById(competitionId).getSuccess().getCompetitionEoiEvidenceConfigResource().getId();
        List<String> validMediaTypesForEoiEvidence = competitionEoiEvidenceConfigService.getValidMediaTypesForEoiEvidence(eoiEvidenceConfigId).getSuccess();
        UserResource userResource = getUserById(userId).getSuccess();

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename,
                fileValidator, validMediaTypesForEoiEvidence, maxFilesizeBytesForEoiEvidenceResponse, request,
                (fileAttributes, inputStreamSupplier) ->
                        applicationEoiEvidenceResponseService.createEoiEvidenceFileEntry(applicationId, organisationId, userResource, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

//        long competitionId = applicationService.getApplicationById(applicationId).getSuccess().getCompetition();
//        long eoiEvidenceConfigId = competitionService.getCompetitionById(competitionId).getSuccess().getCompetitionEoiEvidenceConfigResource().getId();
////        List<String> validMediaTypes = Collections.singletonList(competitionEoiDocumentRepository.findById(eoiEvidenceConfigId).get().getFileType().getName());
//        ServiceResult<FileEntry> fileCreated = fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename,
//                fileValidator, validMediaTypes, maxFilesizeBytesForEoiEvidenceResponse, request,
//                (fileAttributes, inputStreamSupplier) ->
//                        fileService.createFile(fileAttributes.toFileEntryResource(), inputStreamSupplier)).toServiceResult();
//
//        return applicationEoiEvidenceResponseService.create(new ApplicationEoiEvidenceResponseResource(applicationId, organisationId, fileCreated.getSuccess().getId())).toGetResponse();
//    }

    @PostMapping("/eoi-evidence-response/submit")
    public RestResult<Void> submitEoiEvidence(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource,
                                              UserResource userResource) {
        return applicationEoiEvidenceResponseService.submit(applicationEoiEvidenceResponseResource, userResource).toGetResponse();
    }

    @GetMapping ("/{applicationId}/eoi-evidence-response")
    public RestResult <Optional<ApplicationEoiEvidenceResponseResource>> findOneByApplicationId(@PathVariable("applicationId") long applicationId) {
        return applicationEoiEvidenceResponseService.findOneByApplicationId(applicationId).toGetResponse();
    }

    @DeleteMapping("/{applicationId}/eoi-evidence-response/delete/{fileEntryId}")
    public RestResult<ApplicationEoiEvidenceResponseResource> remove(@PathVariable("applicationId") long applicationId,
                                   @PathVariable("fileEntryId") long fileEntryId,
                                   long organisationId,
                                   UserResource userResource) {

        ApplicationEoiEvidenceResponseResource eoiEvidenceResponseResource = new ApplicationEoiEvidenceResponseResource();
        eoiEvidenceResponseResource.setApplicationId(applicationId);
        eoiEvidenceResponseResource.setOrganisationId(organisationId);
        eoiEvidenceResponseResource.setFileEntryId(fileEntryId);
        return applicationEoiEvidenceResponseService.remove(eoiEvidenceResponseResource, userResource).toGetResponse();
    }

    public ServiceResult<UserResource> getUserById(long id) {
        return find(userRepository.findById(id), notFoundError(UserResource.class, id)).andOnSuccessReturn(userMapper::mapToResource);
    }

}
