package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.EmployeesAndTurnover;
import org.innovateuk.ifs.finance.domain.GrowthTable;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.handler.OrganisationTypeFinanceHandler;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.EmployeesAndTurnoverRepository;
import org.innovateuk.ifs.finance.repository.GrowthTableRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.COLLABORATIVE;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationFinanceServiceImpl extends AbstractFinanceService<ApplicationFinance, ApplicationFinanceResource> implements ApplicationFinanceService {

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationFinanceMapper applicationFinanceMapper;

    @Autowired
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private EmployeesAndTurnoverRepository employeesAndTurnoverRepository;

    @Autowired
    private GrowthTableRepository growthTableRepository;

    @Override
    @Transactional
    public ServiceResult<ApplicationFinanceResource> findApplicationFinanceByApplicationIdAndOrganisation(long applicationId, long organisationId) {
        Optional<ApplicationFinance> finance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
        if (!finance.isPresent()) {
            return createApplicationFinance(applicationId, organisationId);
        }
        return serviceSuccess(applicationFinanceMapper.mapToResource(finance.get()));
    }

    @Override
    public ServiceResult<List<ApplicationFinanceResource>> findApplicationFinanceByApplication(long applicationId) {
        return find(applicationFinanceRepository.findByApplicationId(applicationId), notFoundError(ApplicationFinance.class, applicationId)).andOnSuccessReturn(applicationFinances -> {
            List<ApplicationFinanceResource> applicationFinanceResources = new ArrayList<>();
            if (applicationFinances != null) {
                applicationFinances.stream().forEach(af -> applicationFinanceResources.add(applicationFinanceMapper.mapToResource(af)));
            }
            return applicationFinanceResources;
        });
    }

    @Override
    public ServiceResult<Double> getResearchParticipationPercentage(long applicationId) {
        return getResearchPercentage(applicationId).andOnSuccessReturn(BigDecimal::doubleValue);
    }

    @Override
    public ServiceResult<List<ApplicationFinanceResource>> financeTotals(long applicationId) {
        return getFinanceTotals(applicationId);
    }

    @Override
    public ServiceResult<ApplicationFinanceResource> getApplicationFinanceById(long applicationFinanceId) {
        return find(applicationFinance(applicationFinanceId)).andOnSuccess(finance -> serviceSuccess(applicationFinanceMapper.mapToResource(finance)));
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationFinanceResource> financeDetails(long applicationId, long organisationId) {
        Optional<ApplicationFinance> finance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
        if (!finance.isPresent()) {
            ServiceResult<ApplicationFinanceResource> result = createApplicationFinance(applicationId, organisationId);
            if (result.isFailure()) {
                return result;
            }
        }
        ApplicationFinanceResourceId applicationFinanceResourceId = new ApplicationFinanceResourceId(applicationId, organisationId);
        return getApplicationFinanceForOrganisation(applicationFinanceResourceId);
    }

    @Override
    public ServiceResult<List<ApplicationFinanceResource>> financeDetails(long applicationId) {
        return find(applicationFinanceHandler.getApplicationFinances(applicationId), notFoundError(ApplicationFinance.class, applicationId));
    }

    private ServiceResult<ApplicationFinanceResource> createApplicationFinance(long applicationId, long organisationId) {
        if (organisationExists(applicationId, organisationId)) {
            return getOpenApplication(applicationId).andOnSuccess(application ->
                    find(organisation(organisationId)).andOnSuccess(organisation -> {
                        ApplicationFinance applicationFinance = applicationFinanceRepository.save(new ApplicationFinance(application, organisation));
                        if (TRUE.equals(application.getCompetition().getIncludeProjectGrowthTable())) {
                            applicationFinance.setGrowthTable(new GrowthTable());
                            growthTableRepository.save(applicationFinance.getGrowthTable());
                        } else {
                            applicationFinance.setEmployeesAndTurnover(new EmployeesAndTurnover());
                            employeesAndTurnoverRepository.save(applicationFinance.getEmployeesAndTurnover());
                        }
                        initialize(applicationFinance);
                        return serviceSuccess(applicationFinanceMapper.mapToResource(applicationFinance));
                    })
            );
        }
        throw new IFSRuntimeException("organisations doesn't exist on application");
    }

    private boolean organisationExists(long applicationId, long organisationId) {
        return organisationService.findByApplicationId(applicationId)
                .getOptionalSuccessObject()
                .map(organisations -> organisations.stream().anyMatch(org -> org.getId().equals(organisationId)))
                .orElse(false);
    }


    @Override
    @Transactional
    public ServiceResult<ApplicationFinanceResource> updateApplicationFinance(long applicationFinanceId, ApplicationFinanceResource applicationFinance) {
        return getOpenApplication(applicationFinance.getApplication()).andOnSuccess(app ->
                find(applicationFinance(applicationFinanceId)).andOnSuccess(dbFinance -> {
                    updateFinanceDetails(dbFinance, applicationFinance);
                    if (applicationFinance.getWorkPostcode() != null) {
                        dbFinance.setWorkPostcode(applicationFinance.getWorkPostcode());
                    }
                    if (applicationFinance.getInternationalLocation() != null) {
                        dbFinance.setInternationalLocation(applicationFinance.getInternationalLocation());
                    }
                    Long financeFileEntryId = applicationFinance.getFinanceFileEntry();
                    dbFinance = setFinanceUpload(dbFinance, financeFileEntryId);
                    dbFinance = applicationFinanceRepository.save(dbFinance);
                    return serviceSuccess(applicationFinanceMapper.mapToResource(dbFinance));
                })
        );
    }

    /**
     * There are some objects that need a default value, and an instance to use in the form,
     * so there are some objects that need to be created before loading the form.
     */
    private void initialize(ApplicationFinance applicationFinance) {
        OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getApplication().getCompetition().getId(), applicationFinance.getOrganisation().getOrganisationType().getId());

        for (FinanceRowType costType : applicationFinance.getApplication().getCompetition().getFinanceRowTypes()) {
            organisationFinanceHandler.initialiseCostType(applicationFinance, costType);
        }
    }

    private ApplicationFinance setFinanceUpload(ApplicationFinance applicationFinance, Long fileEntryId) {
        if (fileEntryId == null || fileEntryId == 0L) {
            applicationFinance.setFinanceFileEntry(null);
        } else {
            Optional<FileEntry> fileEntry = fileEntryRepository.findById(fileEntryId);
            if (fileEntry.isPresent()) {
                applicationFinance.setFinanceFileEntry(fileEntry.get());
            }
        }
        return applicationFinance;
    }

    @Override
    public ServiceResult<Boolean> collaborativeFundingCriteriaMet(long applicationId) {
        return getApplication(applicationId).andOnSuccess(application -> {
            Competition competition = application.getCompetition();
            if (competition.getCollaborationLevel() == COLLABORATIVE) {
                return getFinanceTotals(applicationId).andOnSuccessReturn(financeTotals -> {
                    long numberSeekingFunding = financeTotals
                            .stream()
                            .filter(financeTotal -> financeTotal.getTotalFundingSought().compareTo(BigDecimal.ZERO) > 0)
                            .count();

                    return numberSeekingFunding > 1;
                });
            } else {
                return serviceSuccess(true);
            }
        });
    }

    private ServiceResult<BigDecimal> getResearchPercentage(Long applicationId) {
        return find(applicationFinanceHandler.getResearchParticipationPercentage(applicationId), notFoundError(Application.class, applicationId));
    }

    private ServiceResult<List<ApplicationFinanceResource>> getFinanceTotals(Long applicationId) {
        return serviceSuccess(applicationFinanceHandler.getApplicationTotals(applicationId));
    }

    private ServiceResult<ApplicationFinanceResource> getApplicationFinanceForOrganisation(ApplicationFinanceResourceId applicationFinanceResourceId) {
        return serviceSuccess(applicationFinanceHandler.getApplicationOrganisationFinances(applicationFinanceResourceId));
    }

    private Supplier<ServiceResult<ApplicationFinance>> applicationFinance(Long applicationFinanceId) {
        return () -> getApplicationFinance(applicationFinanceId);
    }

    private ServiceResult<ApplicationFinance> getApplicationFinance(Long applicationFinanceId) {
        return find(applicationFinanceRepository.findById(applicationFinanceId), notFoundError(ApplicationFinance.class, applicationFinanceId));
    }
}
