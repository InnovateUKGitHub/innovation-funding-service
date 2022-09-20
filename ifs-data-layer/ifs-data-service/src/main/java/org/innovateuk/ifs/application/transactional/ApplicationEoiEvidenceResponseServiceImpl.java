package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationEoiEvidenceResponse;
import org.innovateuk.ifs.application.mapper.ApplicationEoiEvidenceResponseMapper;
import org.innovateuk.ifs.application.repository.ApplicationEoiEvidenceResponseRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationEoiEvidenceWorkflowHandler;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionEoiEvidenceConfig;
import org.innovateuk.ifs.competition.transactional.CompetitionEoiEvidenceConfigService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationEoiEvidenceResponseServiceImpl extends BaseTransactionalService implements ApplicationEoiEvidenceResponseService{

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationEoiEvidenceResponseRepository applicationEoiEvidenceResponseRepository;

    @Autowired
    private ApplicationEoiEvidenceResponseMapper applicationEoiEvidenceResponseMapper;

    @Autowired
    private ApplicationEoiEvidenceWorkflowHandler applicationEoiEvidenceWorkflowHandler;

    @Autowired
    private CompetitionEoiEvidenceConfigService competitionEoiEvidenceConfigService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public ServiceResult<ApplicationEoiEvidenceResponseResource> create(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource) {
        Long applicationId = applicationEoiEvidenceResponseResource.getApplicationId();
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccess((application) -> {
                    if (application.isSubmitted()) {
                        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = applicationEoiEvidenceResponseMapper.mapToDomain(applicationEoiEvidenceResponseResource);
                        applicationEoiEvidenceResponse = applicationEoiEvidenceResponseRepository.save(applicationEoiEvidenceResponse);
                        return initialiseApplicationEoiEvidenceWorkflow(application, applicationEoiEvidenceResponse)
                                .andOnSuccess(initialisedApplicationEoiEvidenceResponse -> serviceSuccess(applicationEoiEvidenceResponseMapper.mapToResource(initialisedApplicationEoiEvidenceResponse)));
                    } else {
                        return serviceFailure(CommonFailureKeys.APPLICATION_UNABLE_TO_UPLOAD_EOI_EVIDENCE_AS_APPLICATION_NOT_YET_SUBMITTED);
                    }
                });
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationEoiEvidenceResponseResource> createEoiEvidenceFileEntry(long applicationId, long organisationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return find(competitionEoiEvidenceConfig(applicationId))
                .andOnSuccess(config -> isValidFileEntryType(applicationId, fileEntryResource))
                .andOnSuccess(() -> fileService.createFile(fileEntryResource, inputStreamSupplier)
                        .andOnSuccessReturn(fileDetails -> create(new ApplicationEoiEvidenceResponseResource(applicationId, organisationId, fileDetails.getId())).getSuccess()));
    }

    private ServiceResult <CompetitionEoiEvidenceConfig> competitionEoiEvidenceConfig(long applicationId) {
        long competitionId = applicationRepository.findById(applicationId).get().getCompetition().getId();
        return serviceSuccess(competitionRepository.findById(competitionId).get().getCompetitionEoiEvidenceConfig());
    }

    private ServiceResult <Boolean> isValidFileEntryType(long applicationId, FileEntryResource fileEntryResource) {
        long eoiEvidenceConfigId = competitionEoiEvidenceConfig(applicationId).getSuccess().getId();
        return serviceSuccess(competitionEoiEvidenceConfigService.getValidMediaTypesForEoiEvidence(eoiEvidenceConfigId).getSuccess().contains(fileEntryResource.getMediaType()));
    }

    private ServiceResult<ApplicationEoiEvidenceResponse> initialiseApplicationEoiEvidenceWorkflow(Application application, ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse) {
        if (application.getCompetition().isEnabledForPreRegistration()
                && application.getCompetition().isEoiEvidenceRequired()
                && application.isEnabledForExpressionOfInterest()) {
            if (applicationEoiEvidenceWorkflowHandler.documentUploaded(applicationEoiEvidenceResponse)) {
                return serviceSuccess(applicationEoiEvidenceResponse);
            } else {
                return serviceFailure(CommonFailureKeys.APPLICATION_UNABLE_TO_INITIALISE_EOI_EVIDENCE_UPLOAD);
            }
        } else {
            return serviceFailure(CommonFailureKeys.APPLICATION_NOT_ENABLED_FOR_EOI_EVIDENCE_UPLOAD);
        }
    }

    @Override
    @Transactional
    public ServiceResult<Void> submit(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource) {
        Long applicationId = applicationEoiEvidenceResponseResource.getApplicationId();
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccess((application) -> {
                    Optional<ApplicationEoiEvidenceResponse> optionalApplicationEoiEvidenceResponse = applicationEoiEvidenceResponseRepository.findOneByApplicationId(application.getId());
                    if (optionalApplicationEoiEvidenceResponse.isPresent()) {
                        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = optionalApplicationEoiEvidenceResponse.get();
                        ProcessRole processRole = application.getLeadApplicantProcessRole();
                        User user = userMapper.mapToDomain(userResource);
                        return applicationEoiEvidenceWorkflowHandler.submit(applicationEoiEvidenceResponse, processRole, user)
                                ? serviceSuccess()
                                : serviceFailure(CommonFailureKeys.APPLICATION_UNABLE_TO_SUBMIT_EOI_EVIDENCE_UPLOAD);
                    } else {
                        return serviceFailure(CommonFailureKeys.APPLICATION_UNABLE_TO_FIND_UPLOADED_EOI_EVIDENCE);
                    }
                });
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource) {
        Long applicationId = applicationEoiEvidenceResponseResource.getApplicationId();
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccess(application -> {
                    Optional<ApplicationEoiEvidenceResponse> optionalApplicationEoiEvidenceResponse = applicationEoiEvidenceResponseRepository.findOneByApplicationId(application.getId());
                    if (optionalApplicationEoiEvidenceResponse.isPresent()) {
                        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = optionalApplicationEoiEvidenceResponse.get();
                        applicationEoiEvidenceResponseRepository.delete(applicationEoiEvidenceResponse);
                    }

                    return serviceFailure(CommonFailureKeys.APPLICATION_UNABLE_TO_FIND_UPLOADED_EOI_EVIDENCE);
                });
    }

    @Override
    public ServiceResult <Optional<ApplicationEoiEvidenceResponseResource>> findOneByApplicationId(long applicationId) {
        return serviceSuccess(applicationEoiEvidenceResponseRepository.findOneByApplicationId(applicationId).map(applicationEoiEvidenceResponseMapper::mapToResource));
    }
}
