package org.innovateuk.ifs.finance.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.finance.domain.*;
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
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class FinanceRowCostsServiceImpl extends BaseTransactionalService implements FinanceRowCostsService {

    private static final Log LOG = LogFactory.getLog(FinanceRowCostsServiceImpl.class);

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
    @Transactional
    public ServiceResult<FinanceRowItem> addCost(final Long applicationFinanceId, final Long questionId, final FinanceRowItem newCostItem) {
        return find(question(questionId), applicationFinance(applicationFinanceId)).andOnSuccess((question, applicationFinance) ->
                getOpenApplication(applicationFinance.getApplication().getId()).andOnSuccess(application -> {
                    OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getOrganisation().getOrganisationType().getId());
                    if (newCostItem != null) {
                        FinanceRow newCost = addCostItem(applicationFinance, question, newCostItem);
                        return serviceSuccess(organisationFinanceHandler.costToCostItem((ApplicationFinanceRow)newCost));
                    } else {
                        ApplicationFinanceRow cost = new ApplicationFinanceRow(applicationFinance, question);
                        organisationFinanceHandler.addCost(cost.getTarget().getId(), cost.getQuestion().getId(), cost);
                        return serviceSuccess(organisationFinanceHandler.costToCostItem(cost));
                    }
                })
        );
    }

    @Override
    public ServiceResult<FinanceRowItem> addCostWithoutPersisting(final Long applicationFinanceId, final Long questionId) {
        return find(question(questionId), applicationFinance(applicationFinanceId)).andOnSuccess((question, applicationFinance) ->
                getOpenOrLaterApplication(applicationFinance.getApplication().getId()).andOnSuccess(application -> {
                    OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getOrganisation().getOrganisationType().getId());
                    FinanceRow cost = new ApplicationFinanceRow(applicationFinance, question);
                    return serviceSuccess(organisationFinanceHandler.costToCostItem((ApplicationFinanceRow)cost));
                })
        );
    }

    @Override
    @Transactional
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

                    ApplicationFinanceRow savedCost = organisationFinanceHandler.updateCost(updatedCost);

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
    @Transactional
    public ServiceResult<Void> deleteCost(final Long costId) {
        Application application = financeRowRepository.findOne(costId).getTarget().getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app -> {
            financeRowMetaValueRepository.deleteByFinanceRowId(costId);
            financeRowRepository.delete(costId);
            return serviceSuccess();
        });
    }

    @Override
    @Transactional
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
    @Transactional
    public ServiceResult<ApplicationFinanceResource> updateApplicationFinance(Long applicationFinanceId, ApplicationFinanceResource applicationFinance) {
        Application application = applicationRepository.findOne(applicationFinance.getApplication());
        return getOpenApplication(application.getId()).andOnSuccess(app ->
                find(applicationFinance(applicationFinanceId)).andOnSuccess(dbFinance -> {
                    if (applicationFinance.getOrganisationSize() != null) {
                        dbFinance.setOrganisationSize(organisationSizeRepository.findOne(applicationFinance.getOrganisationSize()));
                    }
                    if (applicationFinance.getProjectLocation() != null) {
                        dbFinance.setProjectLocation(applicationFinance.getProjectLocation());
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

    private FinanceRow addCostItem(ApplicationFinance applicationFinance, Question question, FinanceRowItem newCostItem) {
        OrganisationFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getOrganisation().getOrganisationType().getId());

        FinanceRow cost = organisationFinanceHandler.costItemToCost(newCostItem);
        cost.setQuestion(question);
        cost.setTarget(applicationFinance);

        return persistCostHandlingCostValues(cost, organisationFinanceHandler);
    }

    private ApplicationFinanceRow persistCostHandlingCostValues(FinanceRow cost, OrganisationFinanceHandler financeHandler) {

        List<FinanceRowMetaValue> costValues = cost.getFinanceRowMetadata();
        cost.setFinanceRowMetadata(new ArrayList<>());
        ApplicationFinanceRow persistedCost = financeHandler.addCost(cost.getTarget().getId(), cost.getQuestion().getId(),(ApplicationFinanceRow)cost);
        costValues.stream().forEach(costVal -> costVal.setFinanceRowId(persistedCost.getId()));
        persistedCost.setFinanceRowMetadata(costValues);
        financeRowMetaValueRepository.save(costValues);
        return financeHandler.updateCost(persistedCost);
    }

    private ApplicationFinanceRow mapCost(ApplicationFinanceRow currentCost, ApplicationFinanceRow newCost) {
        if (newCost.getCost() != null ||  costIsForOtherFunding(newCost)) {
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

    private boolean costIsForOtherFunding(ApplicationFinanceRow cost) {
        // respect null values for other funding costs, in order to produce correct validation messages
        return cost.getName() != null &&
                cost.getName().equals("other-funding");
    }

    private void updateOrCreateCostValue(FinanceRowMetaValue newMetaValue, FinanceRow savedCost) {
        if (newMetaValue.getFinanceRowMetaField() == null) {
            LOG.error("FinanceRowMetaField is null");
            return;
        }
        newMetaValue.setFinanceRowId(savedCost.getId());

        getMetaValueByFieldForFinanceRow(newMetaValue, savedCost)
                .andOnSuccessReturnVoid(currentMetaValue -> updateCostValue(currentMetaValue, newMetaValue))
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