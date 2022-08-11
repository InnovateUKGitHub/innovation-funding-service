package org.innovateuk.ifs.application.transactional;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationExpressionOfInterestConfig;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaValueRepository;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.horizon.repository.ApplicationHorizonWorkProgrammeRepository;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.UUID;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Slf4j
@Service
public class ApplicationEoiServiceImpl implements ApplicationEoiService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationExpressionOfInterestConfigRepository applicationExpressionOfInterestConfigRepository;

    @Autowired
    private ApplicationHorizonWorkProgrammeRepository applicationHorizonWorkProgrammeRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Autowired
    private FinanceRowMetaValueRepository financeRowMetaValueRepository;

    @Autowired
    private ApplicationOrganisationAddressRepository applicationOrganisationAddressRepository;

    @Autowired
    private AverageAssessorScoreRepository averageAssessorScoreRepository;

    @Autowired
    private EuGrantTransferRepository euGrantTransferRepository;

    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    private ApplicationProcessRepository applicationProcessRepository;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationProgressService applicationProgressService;

    @Autowired
    private ApplicationMigrationService applicationMigrationService;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public ServiceResult<Long> createFullApplicationFromEoi(long applicationId) {
        return applicationMigrationService.migrateApplication(applicationId, false)
                .andOnSuccess(migratedApplicationId -> find(applicationRepository.findById(migratedApplicationId), notFoundError(Application.class, migratedApplicationId))
                        .andOnSuccess(migratedApplication -> {
                            ApplicationResource applicationResource = applicationService.getApplicationById(applicationId).getSuccess();

                            return find(applicationRepository.findById(applicationResource.getId()), notFoundError(Application.class, applicationResource.getId()))
                                    .andOnSuccess(application -> {
                                        populateApplicationDetails(application, migratedApplication);

                                        populateApplicationExpressionOfInterestConfig(application, migratedApplication);

                                        populateApplicationHorizonWorkProgramme(application, migratedApplication);

                                        populateApplicationFinance(application, migratedApplication);

                                        populateApplicationOrganisationAddress(application, migratedApplication);

                                        populateAverageAssessorScore(application, migratedApplication);

                                        populateEuGrantTransfer(application, migratedApplication);

                                        populateProcessRole(application, migratedApplication);

                                        populateFormInputResponse(application, migratedApplication);

                                        populateQuestionStatus(application, migratedApplication);

                                        populateApplicationInvite(application, migratedApplication);

                                        updateApplicationProgress(application);

                                        markApplicationInProgress(application);

                                        return serviceSuccess(migratedApplicationId);
                                    });
                        })
                );
    }

    private void markApplicationInProgress(Application application) {
        ApplicationProcess process = new ApplicationProcess(application, null, ApplicationState.OPENED);
        applicationProcessRepository.save(process);

        log.debug("Added application process for application : " + application.getId());
    }

    private void updateApplicationProgress(Application application) {
        applicationProgressService.updateApplicationProgress(application.getId());

        log.debug("Recalculated progress for updated application : " + application.getId());
    }

    private void populateApplicationInvite(Application application, Application migratedApplication) {
        applicationInviteRepository.findByApplicationId(migratedApplication.getId()).stream()
                .forEach(applicationInvite -> {
                    em.detach(applicationInvite);
                    applicationInvite.setId(null);
                    applicationInvite.setHash(UUID.randomUUID().toString());
                    applicationInvite.setTarget(application);
                    applicationInviteRepository.save(applicationInvite);
                });

        log.debug("Populate application invite for application : " + application.getId());
    }

    private void populateQuestionStatus(Application application, Application migratedApplication) {
        questionStatusRepository.findByApplicationId(migratedApplication.getId()).stream()
                .forEach(questionStatus -> {
                    em.detach(questionStatus);
                    questionStatus.setId(null);
                    questionStatus.setApplication(application);
                    questionStatusRepository.save(questionStatus);
                });

        log.debug("Populate question status for application : " + application.getId());
    }

    private void populateFormInputResponse(Application application, Application migratedApplication) {
        formInputResponseRepository.findByApplicationId(migratedApplication.getId()).stream()
                .forEach(formInputResponse -> {
                    em.detach(formInputResponse);
                    formInputResponse.setId(null);
                    formInputResponse.setApplication(application);

                    FormInput formInput = formInputResponse.getFormInput();
                    if (formInput.getType() == FormInputType.FILEUPLOAD) {
                        formInputResponseRepository.findByApplicationIdAndFormInputId(migratedApplication.getId(), formInput.getId()).stream()
                                .findFirst()
                                .ifPresent(fileEntryFormInputResponse -> fileEntryFormInputResponse.getFileEntries().stream()
                                        .findFirst()
                                        .ifPresent(
                                                fileEntry -> {
                                                    FileEntry clonedFileEntry = new FileEntry(null, fileEntry.getName(), fileEntry.getMediaType(), fileEntry.getFilesizeBytes());
                                                    formInputResponse.setFileEntries(Collections.singletonList(fileEntryRepository.save(clonedFileEntry)));
                                                })
                                );
                    }

                    formInputResponseRepository.save(formInputResponse);
                });

        log.debug("Populate form input response for application : " + application.getId());
    }

    private void populateProcessRole(Application application, Application migratedApplication) {
        processRoleRepository.findByApplicationId(migratedApplication.getId()).stream()
                .forEach(processRole -> {
                    em.detach(processRole);
                    processRole.setId(null);
                    processRole.setApplicationId(application.getId());
                    processRoleRepository.save(processRole);
                });

        log.debug("Populate process role for application : " + application.getId());
    }

    private void populateEuGrantTransfer(Application application, Application migratedApplication) {
        serviceSuccess(euGrantTransferRepository.findByApplicationId(migratedApplication.getId()))
                .andOnSuccessReturnVoid(euGrantTransfer -> {
                    if (euGrantTransfer != null) {
                        em.detach(euGrantTransfer);
                        euGrantTransfer.setId(null);
                        euGrantTransfer.setApplication(application);

                        if (euGrantTransfer.getGrantAgreement() != null) {
                            FileEntry grantAgreement = euGrantTransfer.getGrantAgreement();
                            FileEntry clonedGrantAgreement = new FileEntry(null, grantAgreement.getName(), grantAgreement.getMediaType(), grantAgreement.getFilesizeBytes());
                            euGrantTransfer.setGrantAgreement(fileEntryRepository.save(clonedGrantAgreement));
                        }

                        euGrantTransferRepository.save(euGrantTransfer);
                    }
                });

        log.debug("Populate eu grant transfer for application : " + application.getId());
    }

    private void populateAverageAssessorScore(Application application, Application migratedApplication) {
        averageAssessorScoreRepository.findByApplicationId(migratedApplication.getId()).ifPresent(
                averageAssessorScore -> {
                    em.detach(averageAssessorScore);
                    averageAssessorScore.setId(null);
                    averageAssessorScore.setApplication(application);
                    averageAssessorScoreRepository.save(averageAssessorScore);
                });

        log.debug("Populate average assessor score for application : " + application.getId());
    }

    private void populateApplicationOrganisationAddress(Application application, Application migratedApplication) {
        applicationOrganisationAddressRepository.findByApplicationId(migratedApplication.getId()).stream()
                .forEach(applicationOrganisationAddress -> {
                    em.detach(applicationOrganisationAddress);
                    applicationOrganisationAddress.setId(null);
                    applicationOrganisationAddress.setApplication(application);
                    applicationOrganisationAddressRepository.save(applicationOrganisationAddress);
                });

        log.debug("Populate application organisation address for application : " + application.getId());
    }

    private void populateApplicationFinance(Application application, Application migratedApplication) {
        applicationFinanceRepository.findByApplicationId(migratedApplication.getId()).stream()
                .forEach(applicationFinance -> {
                    Long applicationFinanceId = applicationFinance.getId();
                    em.detach(applicationFinance);
                    applicationFinance.setId(null);
                    applicationFinance.setApplication(application);
                    ApplicationFinance clonedApplicationFinance = applicationFinanceRepository.save(applicationFinance);

                    applicationFinanceRowRepository.findByTargetId(applicationFinanceId).stream()
                            .forEach(applicationFinanceRow -> {
                                em.detach(applicationFinanceRow);
                                applicationFinanceRow.setId(null);
                                applicationFinanceRow.setTarget(clonedApplicationFinance);
                                ApplicationFinanceRow savedClonedApplicationFinanceRow = applicationFinanceRowRepository.save(applicationFinanceRow);

                                financeRowMetaValueRepository.financeRowId(applicationFinanceRow.getId()).stream()
                                        .forEach(financeRowMetaValue -> {
                                            em.detach(financeRowMetaValue);
                                            financeRowMetaValue.setId(null);
                                            financeRowMetaValue.setFinanceRowId(savedClonedApplicationFinanceRow.getId());
                                        });
                            });
                });

        log.debug("Populate application finance for application : " + application.getId());
    }

    private void populateApplicationHorizonWorkProgramme(Application application, Application migratedApplication) {
        if (application.getCompetition().isHorizonEuropeGuarantee()) {
            applicationHorizonWorkProgrammeRepository.findByApplicationId(migratedApplication.getId()).stream()
                    .forEach(applicationHorizonWorkProgramme -> {
                        em.detach(applicationHorizonWorkProgramme);
                        applicationHorizonWorkProgramme.setId(null);
                        applicationHorizonWorkProgramme.setApplicationId(application.getId());
                        applicationHorizonWorkProgrammeRepository.save(applicationHorizonWorkProgramme);
                    });

            log.debug("Populate Horizon Work Programme for application : " + application.getId());
        }
    }

    private void populateApplicationExpressionOfInterestConfig(Application application, Application migratedApplication) {
        if (migratedApplication.getApplicationExpressionOfInterestConfig() != null) {
            Long applicationExpressionOfInterestConfigId =  migratedApplication.getApplicationExpressionOfInterestConfig().getId();
            applicationExpressionOfInterestConfigRepository.findById(applicationExpressionOfInterestConfigId).ifPresent(
                    applicationExpressionOfInterestConfig -> {
                        ApplicationExpressionOfInterestConfig fullApplicationExpressionOfInterestConfig = ApplicationExpressionOfInterestConfig.builder()
                                .application(application)
                                .enabledForExpressionOfInterest(false)
                                .eoiApplicationId(migratedApplication.getId())
                                .build();
                        applicationExpressionOfInterestConfigRepository.save(fullApplicationExpressionOfInterestConfig);
                        application.setApplicationExpressionOfInterestConfig(fullApplicationExpressionOfInterestConfig);
                    });

            log.debug("Populate application expression of interest config for application : " + application.getId());
        }
    }

    private void populateApplicationDetails(Application application, Application migratedApplication) {
        application.setDurationInMonths(migratedApplication.getDurationInMonths());
        application.setStartDate(migratedApplication.getStartDate());
        application.setResearchCategory(migratedApplication.getResearchCategory());
        application.setDecision(null);
        application.setManageDecisionEmailDate(null);
        application.setFeedbackReleased(null);
        applicationRepository.save(application);

        log.debug("Populate additional details for application : " + application.getId());
    }
}
