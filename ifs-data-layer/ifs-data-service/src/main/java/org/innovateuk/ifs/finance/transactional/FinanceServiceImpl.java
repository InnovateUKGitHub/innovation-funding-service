package org.innovateuk.ifs.finance.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.ApplicationFinanceFileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceHandler;
import org.innovateuk.ifs.finance.handler.ProjectFinanceHandler;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_TEAM_STATUS_APPLICATION_FINANCE_RECORD_FOR_APPLICATION_ORGANISATION_DOES_NOT_EXIST;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class FinanceServiceImpl extends BaseTransactionalService implements FinanceService {


    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ApplicationFinanceFileEntryService fileEntryService;

    @Autowired
    private ApplicationFinanceMapper applicationFinanceMapper;

    @Autowired
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Autowired
    private ProjectFinanceHandler projectFinanceHandler;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private FinanceRowCostsService financeRowCostsService;



    @Override
    public ServiceResult<ApplicationFinanceResource> findApplicationFinanceByApplicationIdAndOrganisation(Long applicationId, final Long organisationId) {
        return find(applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId), notFoundError(ApplicationFinance.class, applicationId, organisationId)).
                andOnSuccessReturn(finance -> applicationFinanceMapper.mapToResource(finance));
    }

    @Override
    public ServiceResult<List<ApplicationFinanceResource>> findApplicationFinanceByApplication(Long applicationId) {
        return find(applicationFinanceRepository.findByApplicationId(applicationId), notFoundError(ApplicationFinance.class, applicationId)).andOnSuccessReturn(applicationFinances -> {
            List<ApplicationFinanceResource> applicationFinanceResources = new ArrayList<>();
            if (applicationFinances != null) {
                applicationFinances.stream().forEach(af -> applicationFinanceResources.add(applicationFinanceMapper.mapToResource(af)));
            }
            return applicationFinanceResources;
        });
    }

    @Override
    public ServiceResult<Double> getResearchParticipationPercentage(Long applicationId) {
        return getResearchPercentage(applicationId).andOnSuccessReturn(BigDecimal::doubleValue);
    }

    @Override
    public ServiceResult<Double> getResearchParticipationPercentageFromProject(Long projectId) {
        return getResearchPercentageFromProject(projectId).andOnSuccessReturn(BigDecimal::doubleValue);
    }

    @Override
    public ServiceResult<List<ApplicationFinanceResource>> financeTotals(Long applicationId) {
        return getFinanceTotals(applicationId);
    }

    @Override
    public ServiceResult<ApplicationFinanceResource> getApplicationFinanceById(Long applicationFinanceId) {
        return find(applicationFinance(applicationFinanceId)).andOnSuccess(finance -> serviceSuccess(applicationFinanceMapper.mapToResource(finance)));
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createFinanceFileEntry(long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        ApplicationFinance applicationFinance = applicationFinanceRepository.findOne(applicationFinanceId);
        return getOpenApplication(applicationFinance.getApplication().getId()).andOnSuccess(app ->
                fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileResults -> linkFileEntryToApplicationFinance(applicationFinance, fileResults))
        );
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> updateFinanceFileEntry(long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        ApplicationFinance applicationFinance = applicationFinanceRepository.findOne(applicationFinanceId);
        return getOpenApplication(applicationFinance.getApplication().getId()).andOnSuccess(app ->
                fileService.updateFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileResults -> linkFileEntryToApplicationFinance(applicationFinance, fileResults))
        );
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteFinanceFileEntry(long applicationFinanceId) {
        Application application = applicationFinanceRepository.findOne(applicationFinanceId).getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app ->
                getApplicationFinanceById(applicationFinanceId).
                        andOnSuccess(finance -> fileService.deleteFileIgnoreNotFound(finance.getFinanceFileEntry()).
                                andOnSuccess(() -> removeFileEntryFromApplicationFinance(finance))).
                        andOnSuccessReturnVoid()
        );
    }

    @Override
    public ServiceResult<FileAndContents> getFileContents(long applicationFinanceId) {
        return fileEntryService.getFileEntryByApplicationFinanceId(applicationFinanceId)
                .andOnSuccess(fileEntry -> fileService.getFileByFileEntryId(fileEntry.getId())
                        .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntry, inputStream)));
    }

    @Override
    public ServiceResult<ApplicationFinanceResource> financeDetails(Long applicationId, Long organisationId) {
        ApplicationFinanceResourceId applicationFinanceResourceId = new ApplicationFinanceResourceId(applicationId, organisationId);
        return getApplicationFinanceForOrganisation(applicationFinanceResourceId);
    }

    @Override
    public ServiceResult<List<ApplicationFinanceResource>> financeDetails(Long applicationId) {
        return find(applicationFinanceHandler.getApplicationFinances(applicationId), notFoundError(ApplicationFinance.class, applicationId));
    }

    @Override
    public ServiceResult<Boolean> organisationSeeksFunding(Long projectId, Long applicationId, Long organisationId) {
        ApplicationFinanceResourceId applicationFinanceResourceId = new ApplicationFinanceResourceId(applicationId, organisationId);

        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(
                applicationFinanceResourceId.getApplicationId(), applicationFinanceResourceId.getOrganisationId());

        if(applicationFinance != null) {
            OrganisationType organisationType = organisationRepository.findOne(organisationId).getOrganisationType();

            if(isAcademic(organisationType)){   // Academic organisations will always be funded.
                return serviceSuccess(true);
            } else {
                //TODO: INFUND-5102 This to me seems like a very messy way of building resource object. You don't only need to map the domain object using the mapper, but then also do a bunch of things in setFinanceDetails.  We should find a better way to handle this.
                ApplicationFinanceResource applicationFinanceResource = applicationFinanceMapper.mapToResource(applicationFinance);
                setFinanceDetails(organisationType, applicationFinanceResource, applicationFinance.getApplication().getCompetition());
                return serviceSuccess(applicationFinanceResource.getGrantClaimPercentage() != null && applicationFinanceResource.getGrantClaimPercentage() > 0);
            }
        } else {
            return serviceFailure(new Error(PROJECT_TEAM_STATUS_APPLICATION_FINANCE_RECORD_FOR_APPLICATION_ORGANISATION_DOES_NOT_EXIST, asList(applicationId, organisationId)));
        }
    }



    private ServiceResult<BigDecimal> getResearchPercentageFromProject(Long projectId) {
        return find(projectFinanceHandler.getResearchParticipationPercentageFromProject(projectId), notFoundError(Project.class, projectId));
    }

    private ServiceResult<BigDecimal> getResearchPercentage(Long applicationId) {
        return find(applicationFinanceHandler.getResearchParticipationPercentage(applicationId), notFoundError(Application.class, applicationId));
    }

    private ServiceResult<List<ApplicationFinanceResource>> getFinanceTotals(Long applicationId) {
        return find(applicationFinanceHandler.getApplicationTotals(applicationId), notFoundError(ApplicationFinance.class, applicationId));
    }

    private ServiceResult<ApplicationFinanceResource> getApplicationFinanceForOrganisation(ApplicationFinanceResourceId applicationFinanceResourceId) {
        return serviceSuccess(applicationFinanceHandler.getApplicationOrganisationFinances(applicationFinanceResourceId));
    }

    private FileEntryResource linkFileEntryToApplicationFinance(ApplicationFinance applicationFinance, Pair<File, FileEntry> fileResults) {
        FileEntry fileEntry = fileResults.getValue();

        ApplicationFinanceResource applicationFinanceResource = applicationFinanceMapper.mapToResource(applicationFinance);

        if (applicationFinanceResource != null) {
            applicationFinanceResource.setFinanceFileEntry(fileEntry.getId());
            financeRowCostsService.updateCost(applicationFinanceResource.getId(), applicationFinanceResource);
        }

        return fileEntryMapper.mapToResource(fileEntry);
    }

    private Supplier<ServiceResult<ApplicationFinance>> applicationFinance(Long applicationFinanceId) {
        return () -> getApplicationFinance(applicationFinanceId);
    }


    private ServiceResult<ApplicationFinanceResource> removeFileEntryFromApplicationFinance(ApplicationFinanceResource applicationFinanceResource) {
        Application application = applicationFinanceRepository.findOne(applicationFinanceResource.getId()).getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app -> {
            applicationFinanceResource.setFinanceFileEntry(null);
            return financeRowCostsService.updateCost(applicationFinanceResource.getId(), applicationFinanceResource);
        });
    }

    private boolean isAcademic(OrganisationType type) {
        return OrganisationTypeEnum.RESEARCH.getId().equals(type.getId());
    }

    private void setFinanceDetails(OrganisationType organisationType, ApplicationFinanceResource applicationFinanceResource, Competition competition) {
        OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(organisationType.getId());
        Map<FinanceRowType, FinanceRowCostCategory> costs = organisationFinanceHandler.getOrganisationFinances(applicationFinanceResource.getId(), competition);
        applicationFinanceResource.setFinanceOrganisationDetails(costs);
    }

    private ServiceResult<ApplicationFinance> getApplicationFinance(Long applicationFinanceId) {
        return find(applicationFinanceRepository.findOne(applicationFinanceId), notFoundError(ApplicationFinance.class, applicationFinanceId));
    }




}
