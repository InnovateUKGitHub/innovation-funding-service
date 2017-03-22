package org.innovateuk.ifs.finance.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceHandler;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceRowMapper;
import org.innovateuk.ifs.finance.mapper.FinanceRowMetaFieldMapper;
import org.innovateuk.ifs.finance.repository.*;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class FinanceRowServiceImpl extends BaseTransactionalService implements FinanceRowService {

    private static final Log LOG = LogFactory.getLog(FinanceRowServiceImpl.class);

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Autowired
    private FinanceRowMetaFieldMapper financeRowMetaFieldMapper;

    @Autowired
    private ApplicationFinanceMapper applicationFinanceMapper;

    @Autowired
    private ApplicationFinanceRowMapper applicationFinanceRowMapper;

    @Autowired
    private ApplicationFinanceRowRepository financeRowRepository;

    @Autowired
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Autowired
    private FinanceRowMetaValueRepository financeRowMetaValueRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private OrganisationSizeRepository organisationSizeRepository;

    @Override
    public ServiceResult<FinanceRowMetaField> getCostFieldById(Long id) {
        return find(financeRowMetaFieldRepository.findOne(id), notFoundError(FinanceRowMetaField.class, id));
    }

    @Override
    public ServiceResult<List<FinanceRowMetaFieldResource>> findAllCostFields() {
        List<FinanceRowMetaField> allFinanceRowMetaFields = financeRowMetaFieldRepository.findAll();
        List<FinanceRowMetaFieldResource> resources = simpleMap(allFinanceRowMetaFields, financeRowMetaFieldMapper::mapToResource);
        return serviceSuccess(resources);
    }

    @Override
    public ServiceResult<FinanceRowItem> getCostItem(final Long costItemId) {
        ApplicationFinanceRow cost = financeRowRepository.findOne(costItemId);
        ApplicationFinance applicationFinance = cost.getTarget();
        OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getOrganisation().getOrganisationType().getId());

        return serviceSuccess(organisationFinanceHandler.costToCostItem(cost));
    }

    @Override
    public ServiceResult<FinanceRowItem> addCost(final Long applicationFinanceId, final Long questionId, final FinanceRowItem newCostItem) {
        return find(question(questionId), applicationFinance(applicationFinanceId)).andOnSuccess((question, applicationFinance) ->
                getOpenApplication(applicationFinance.getApplication().getId()).andOnSuccess(application -> {
                    OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getOrganisation().getOrganisationType().getId());
                    if (newCostItem != null) {
                        FinanceRow newCost = addCostItem(applicationFinance, question, newCostItem);
                        return serviceSuccess(organisationFinanceHandler.costToCostItem((ApplicationFinanceRow)newCost));
                    } else {
                        ApplicationFinanceRow cost = new ApplicationFinanceRow(applicationFinance, question);
                        financeRowRepository.save(cost);
                        return serviceSuccess(organisationFinanceHandler.costToCostItem(cost));
                    }
                })
        );
    }

    @Override
    public ServiceResult<FinanceRowItem> addCostWithoutPersisting(final Long applicationFinanceId, final Long questionId) {
        return find(question(questionId), applicationFinance(applicationFinanceId)).andOnSuccess((question, applicationFinance) ->
                getOpenApplication(applicationFinance.getApplication().getId()).andOnSuccess(application -> {
                    OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getOrganisation().getOrganisationType().getId());
                    FinanceRow cost = new ApplicationFinanceRow(applicationFinance, question);
                    return serviceSuccess(organisationFinanceHandler.costToCostItem((ApplicationFinanceRow)cost));
                })
        );
    }

    @Override
    public ServiceResult<FinanceRowItem> updateCost(final Long id, final FinanceRowItem newCostItem) {
        Application application = financeRowRepository.findOne(id).getTarget().getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app ->
                doUpdate(id, newCostItem).andOnSuccessReturn(cost -> {
                    OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(((ApplicationFinanceRow)cost).getTarget().getOrganisation().getOrganisationType().getId());
                    return organisationFinanceHandler.costToCostItem((ApplicationFinanceRow)cost);
                })
        );
    }

    @Override
    public ServiceResult<List<? extends FinanceRow>> getCosts(Long applicationFinanceId, String costTypeName, Long questionId) {
        List<ApplicationFinanceRow> costs = financeRowRepository.findByTargetIdAndNameAndQuestionId(applicationFinanceId, costTypeName, questionId);
        return serviceSuccess(costs);
    }

    @Override
    public ServiceResult<List<FinanceRowItem>> getCostItems(Long applicationFinanceId, String costTypeName, Long questionId) {
        return getApplicationFinance(applicationFinanceId).andOnSuccessReturn(applicationFinance -> {
            OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getOrganisation().getOrganisationType().getId());
            List<ApplicationFinanceRow> costs = financeRowRepository.findByTargetIdAndNameAndQuestionId(applicationFinanceId, costTypeName, questionId);
            return organisationFinanceHandler.costToCostItem(costs);
        });
    }

    @Override
    public ServiceResult<List<FinanceRowItem>> getCostItems(Long applicationFinanceId, Long questionId) {
        return getApplicationFinance(applicationFinanceId).andOnSuccessReturn(applicationFinance -> {
            OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getOrganisation().getOrganisationType().getId());
            List<ApplicationFinanceRow> costs = financeRowRepository.findByTargetIdAndQuestionId(applicationFinanceId, questionId);
            return organisationFinanceHandler.costToCostItem(costs);
        });
    }

    private ServiceResult<FinanceRow> doUpdate(Long id, FinanceRowItem newCostItem) {
        Application application = financeRowRepository.findOne(id).getTarget().getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app ->
                find(cost(id)).andOnSuccessReturn(existingCost -> {
                    ApplicationFinance applicationFinance = existingCost.getTarget();
                    OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getOrganisation().getOrganisationType().getId());
                    ApplicationFinanceRow newCost = organisationFinanceHandler.costItemToCost(newCostItem);
                    ApplicationFinanceRow updatedCost = mapCost(existingCost, newCost);

                    ApplicationFinanceRow savedCost = financeRowRepository.save(updatedCost);

                    newCost.getFinanceRowMetadata()
                            .stream()
                            .filter(c -> c.getValue() != null)
                            .filter(c -> !"null".equals(c.getValue()))
                            .peek(c -> LOG.debug("FinanceRowMetaValue: " + c.getValue()))
                            .forEach(costValue -> updateOrCreateCostValue(costValue, savedCost));

                    // refresh the object, since we need to reload the costvalues, on the cost object.
                    return savedCost;
                })
        );
    }

    @Override
    public ServiceResult<Void> deleteCost(final Long costId) {
        Application application = financeRowRepository.findOne(costId).getTarget().getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app -> {
            financeRowMetaValueRepository.deleteByFinanceRowId(costId);
            financeRowRepository.delete(costId);
            return serviceSuccess();
        });
    }

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

    private ServiceResult<BigDecimal> getResearchPercentageFromProject(Long projectId) {
        return find(applicationFinanceHandler.getResearchParticipationPercentageFromProject(projectId), notFoundError(Project.class, projectId));
    }

    private ServiceResult<BigDecimal> getResearchPercentage(Long applicationId) {
        return find(applicationFinanceHandler.getResearchParticipationPercentage(applicationId), notFoundError(Application.class, applicationId));
    }

    @Override
    public ServiceResult<ApplicationFinanceResource> addCost(final ApplicationFinanceResourceId applicationFinanceResourceId) {
        final Long applicationId = applicationFinanceResourceId.getApplicationId();
        final Long organisationId = applicationFinanceResourceId.getOrganisationId();
        return getOpenApplication(applicationId).andOnSuccess(application -> {
            ApplicationFinance existingFinances = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
            if (existingFinances != null) {
                return serviceSuccess(applicationFinanceMapper.mapToResource(existingFinances));
            }

            return find(organisation(organisationId)).andOnSuccess(organisation -> {

                ApplicationFinance applicationFinance = new ApplicationFinance(application, organisation);

                applicationFinance = applicationFinanceRepository.save(applicationFinance);
                initialize(applicationFinance);
                return serviceSuccess(applicationFinanceMapper.mapToResource(applicationFinance));
            });
        });
    }

    @Override
    public ServiceResult<ApplicationFinanceResource> getApplicationFinanceById(Long applicationFinanceId) {
        return find(applicationFinance(applicationFinanceId)).andOnSuccess(finance -> serviceSuccess(applicationFinanceMapper.mapToResource(finance)));
    }

    @Override
    public ServiceResult<ApplicationFinanceResource> updateCost(Long applicationFinanceId, ApplicationFinanceResource applicationFinance) {
        Application application = applicationRepository.findOne(applicationFinance.getApplication());
        return getOpenApplication(application.getId()).andOnSuccess(app ->
                find(applicationFinance(applicationFinanceId)).andOnSuccess(dbFinance -> {
                    if (applicationFinance.getOrganisationSize() != null) {
                        dbFinance.setOrganisationSize(organisationSizeRepository.findOne(applicationFinance.getOrganisationSize()));
                    }
                    Long financeFileEntryId = applicationFinance.getFinanceFileEntry();
                    dbFinance = setFinanceUpload(dbFinance, financeFileEntryId);
                    dbFinance = applicationFinanceRepository.save(dbFinance);
                    return serviceSuccess(applicationFinanceMapper.mapToResource(dbFinance));
                })
        );
    }

    private ApplicationFinance setFinanceUpload(ApplicationFinance applicationFinance, Long fileEntryId) {
        if (fileEntryId == null || fileEntryId == 0L) {
            applicationFinance.setFinanceFileEntry(null);
        } else {
            FileEntry fileEntry = fileEntryRepository.findOne(fileEntryId);
            if (fileEntry != null) {
                applicationFinance.setFinanceFileEntry(fileEntry);
            }
        }
        return applicationFinance;
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
                setFinanceDetails(organisationType, applicationFinanceResource);
                return serviceSuccess(applicationFinanceResource.getGrantClaimPercentage() != null && applicationFinanceResource.getGrantClaimPercentage() > 0);
            }
        } else {
            return serviceFailure(new Error(PROJECT_TEAM_STATUS_APPLICATION_FINANCE_RECORD_FOR_APPLICATION_ORGANISATION_DOES_NOT_EXIST, asList(applicationId, organisationId)));
        }
    }

    private boolean isAcademic(OrganisationType type) {
        return OrganisationTypeEnum.RESEARCH.getOrganisationTypeId().equals(type.getId());
    }

    private void setFinanceDetails(OrganisationType organisationType, ApplicationFinanceResource applicationFinanceResource) {
        OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(organisationType.getId());
        Map<FinanceRowType, FinanceRowCostCategory> costs = organisationFinanceHandler.getOrganisationFinances(applicationFinanceResource.getId());
        applicationFinanceResource.setFinanceOrganisationDetails(costs);
    }

    @Override
    public ServiceResult<List<ApplicationFinanceResource>> financeTotals(Long applicationId) {
        return getFinanceTotals(applicationId);
    }

    @Override
    public ServiceResult<FileEntryResource> createFinanceFileEntry(long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        Application application = applicationFinanceRepository.findOne(applicationFinanceId).getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app ->
                fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileResults -> linkFileEntryToApplicationFinance(applicationFinanceId, fileResults))
        );
    }

    @Override
    public ServiceResult<FileEntryResource> updateFinanceFileEntry(long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        Application application = applicationFinanceRepository.findOne(applicationFinanceId).getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app ->
                fileService.updateFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileResults -> linkFileEntryToApplicationFinance(applicationFinanceId, fileResults))
        );
    }

    @Override
    public ServiceResult<Void> deleteFinanceFileEntry(long applicationFinanceId) {
        Application application = applicationFinanceRepository.findOne(applicationFinanceId).getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app ->
                getApplicationFinanceById(applicationFinanceId).
                        andOnSuccess(finance -> fileService.deleteFile(finance.getFinanceFileEntry()).
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

    private ServiceResult<ApplicationFinanceResource> removeFileEntryFromApplicationFinance(ApplicationFinanceResource applicationFinanceResource) {
        Application application = applicationFinanceRepository.findOne(applicationFinanceResource.getId()).getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app -> {
            applicationFinanceResource.setFinanceFileEntry(null);
            return updateCost(applicationFinanceResource.getId(), applicationFinanceResource);
        });
    }

    private FileEntryResource linkFileEntryToApplicationFinance(long applicationFinanceId, Pair<File, FileEntry> fileResults) {
        FileEntry fileEntry = fileResults.getValue();

        ApplicationFinanceResource applicationFinanceResource = getApplicationFinanceById(applicationFinanceId).getSuccessObject();

        if (applicationFinanceResource != null) {
            applicationFinanceResource.setFinanceFileEntry(fileEntry.getId());
            updateCost(applicationFinanceResource.getId(), applicationFinanceResource);
        }

        return fileEntryMapper.mapToResource(fileEntry);
    }

    private ServiceResult<List<ApplicationFinanceResource>> getFinanceTotals(Long applicationId) {
        return find(applicationFinanceHandler.getApplicationTotals(applicationId), notFoundError(ApplicationFinance.class, applicationId));
    }

    private ServiceResult<ApplicationFinanceResource> getApplicationFinanceForOrganisation(ApplicationFinanceResourceId applicationFinanceResourceId) {
        return serviceSuccess(applicationFinanceHandler.getApplicationOrganisationFinances(applicationFinanceResourceId));
    }

    private FinanceRow addCostItem(ApplicationFinance applicationFinance, Question question, FinanceRowItem newCostItem) {
        OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getOrganisation().getOrganisationType().getId());

        FinanceRow cost = organisationFinanceHandler.costItemToCost(newCostItem);
        cost.setQuestion(question);
        cost.setTarget(applicationFinance);

        return persistCostHandlingCostValues(cost);
    }

    private ApplicationFinanceRow persistCostHandlingCostValues(FinanceRow cost) {

        List<FinanceRowMetaValue> costValues = cost.getFinanceRowMetadata();
        cost.setFinanceRowMetadata(new ArrayList<>());
        ApplicationFinanceRow persistedCost = financeRowRepository.save((ApplicationFinanceRow)cost);
        costValues.stream().forEach(costVal -> costVal.setFinanceRowId(persistedCost.getId()));
        persistedCost.setFinanceRowMetadata(costValues);
        financeRowMetaValueRepository.save(costValues);
        return financeRowRepository.save(persistedCost);
    }

    private ApplicationFinanceRow mapCost(ApplicationFinanceRow currentCost, ApplicationFinanceRow newCost) {
        if (newCost.getCost() != null) {
            currentCost.setCost(newCost.getCost());
        }
        if (newCost.getDescription() != null) {
            currentCost.setDescription(newCost.getDescription());
        }
        if (newCost.getItem() != null) {
            currentCost.setItem(newCost.getItem());
        }
        if (newCost.getQuantity() != null) {
            currentCost.setQuantity(newCost.getQuantity());
        }

        return currentCost;
    }

    private void updateOrCreateCostValue(FinanceRowMetaValue newMetaValue, FinanceRow savedCost) {
        if (newMetaValue.getFinanceRowMetaField() == null) {
            LOG.error("FinanceRowMetaField is null");
            return;
        }
        newMetaValue.setFinanceRowId(savedCost.getId());

        getMetaValueByFieldForFinanceRow(newMetaValue, savedCost)
                .andOnSuccessReturnVoid(currentMetaValue ->  {    updateCostValue(currentMetaValue, newMetaValue);})
                .andOnFailure(() -> {   createCostValue(newMetaValue, savedCost);
                                        return serviceSuccess(); });

    }

    private void updateCostValue(FinanceRowMetaValue currentMetaValue, FinanceRowMetaValue newMetaValue) {
        currentMetaValue.setValue(newMetaValue.getValue());
        financeRowMetaValueRepository.save(currentMetaValue);
    }

    private void createCostValue(FinanceRowMetaValue newMetaValue, FinanceRow savedCost) {
        FinanceRowMetaField financeRowMetaField = financeRowMetaFieldRepository.findOne(newMetaValue.getFinanceRowMetaField().getId());
        newMetaValue.setFinanceRowMetaField(financeRowMetaField);
        newMetaValue = financeRowMetaValueRepository.save(newMetaValue);
        savedCost.addCostValues(newMetaValue);
    }

    private ServiceResult<FinanceRowMetaValue> getMetaValueByFieldForFinanceRow(FinanceRowMetaValue newMetaValue, FinanceRow savedCost) {
        return find(financeRowMetaValueRepository.financeRowIdAndFinanceRowMetaFieldId(savedCost.getId(), newMetaValue.getFinanceRowMetaField().getId()), notFoundError(FinanceRowMetaValue.class, newMetaValue.getId()));
    }

    private ServiceResult<ApplicationFinanceRow> getCost(Long costId) {
        return find(financeRowRepository.findOne(costId), notFoundError(Question.class));
    }

    private Supplier<ServiceResult<ApplicationFinanceRow>> cost(Long costId) {
        return () -> getCost(costId);
    }


    private Supplier<ServiceResult<ApplicationFinance>> applicationFinance(Long applicationFinanceId) {
        return () -> getApplicationFinance(applicationFinanceId);
    }

    private ServiceResult<ApplicationFinance> getApplicationFinance(Long applicationFinanceId) {
        return find(applicationFinanceRepository.findOne(applicationFinanceId), notFoundError(ApplicationFinance.class, applicationFinanceId));
    }

    /**
     * There are some objects that need a default value, and an instance to use in the form,
     * so there are some objects that need to be created before loading the form.
     */
    private void initialize(ApplicationFinance applicationFinance) {
        OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getOrganisation().getOrganisationType().getId());

        for (FinanceRowType costType : FinanceRowType.values()) {
            organisationFinanceHandler.initialiseCostType(applicationFinance, costType);
        }
    }

    /**
     * Get the cost handler by costItemId. This FinanceRowHandler can be used for validation or conversion of the FinanceRowItem.
     */
    @Override
    public FinanceRowHandler getCostHandler(Long costItemId) {
        FinanceRow cost = applicationFinanceRowMapper.mapIdToDomain(costItemId);
        OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(((ApplicationFinanceRow)cost).getTarget().getOrganisation().getOrganisationType().getId());
        FinanceRowItem costItem = organisationFinanceHandler.costToCostItem((ApplicationFinanceRow)cost);
        FinanceRowHandler financeRowHandler = organisationFinanceHandler.getCostHandler(costItem.getCostType());
        return financeRowHandler;
    }

}
