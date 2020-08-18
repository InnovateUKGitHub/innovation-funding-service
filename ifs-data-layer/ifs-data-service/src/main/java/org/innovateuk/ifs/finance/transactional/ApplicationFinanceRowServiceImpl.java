package org.innovateuk.ifs.finance.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.handler.OrganisationTypeFinanceHandler;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaFieldRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaValueRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FINANCE_TYPE_NOT_SUPPORTED_BY_COMPETITION;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationFinanceRowServiceImpl extends BaseTransactionalService implements ApplicationFinanceRowService {

    private static final Log LOG = LogFactory.getLog(ApplicationFinanceRowServiceImpl.class);

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private ApplicationFinanceRowRepository financeRowRepository;

    @Autowired
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Autowired
    private FinanceRowMetaValueRepository financeRowMetaValueRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Override
    public ServiceResult<FinanceRowItem> get(final long financeRowId) {
        Optional<FinanceRowItem> financeRowItem = financeRowRepository.findById(financeRowId).map(applicationFinanceRow -> {
            ApplicationFinance applicationFinance = applicationFinanceRow.getTarget();
            OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getApplication().getCompetition().getId(), applicationFinance.getOrganisation().getOrganisationType().getId());
            return organisationFinanceHandler.toResource(applicationFinanceRow);
        });
        if (!financeRowItem.isPresent()) {
            LOG.error("IFS-5593 unable to find a FinanceRowItem for financeRowId: " + financeRowId);
        }
        return financeRowItem.map(item -> serviceSuccess(item)).orElse(serviceFailure(GENERAL_NOT_FOUND));
    }

    @Override
    @Transactional
    public ServiceResult<FinanceRowItem> create(long applicationFinanceId, final FinanceRowItem financeRowItem) {
        return find(applicationFinance(financeRowItem.getTargetId())).andOnSuccess(applicationFinance ->
                getOpenApplication(applicationFinance.getApplication().getId()).andOnSuccess(application -> {
                    if (application.getCompetition().getFinanceRowTypes().contains(financeRowItem.getCostType())) {
                        OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getApplication().getCompetition().getId(), applicationFinance.getOrganisation().getOrganisationType().getId());
                        FinanceRow newCost = addCostItem(applicationFinance, financeRowItem);
                        return serviceSuccess(organisationFinanceHandler.toResource((ApplicationFinanceRow) newCost));
                    } else {
                        return serviceFailure(FINANCE_TYPE_NOT_SUPPORTED_BY_COMPETITION);
                    }
                })
        );
    }

    @Override
    @Transactional
    public ServiceResult<FinanceRowItem> update(final long financeRowId, final FinanceRowItem newCostItem) {
        Application application = financeRowRepository.findById(financeRowId).get().getTarget().getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app ->
                doUpdate(financeRowId, newCostItem).andOnSuccessReturn(cost -> {
                    OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(((ApplicationFinanceRow) cost).getTarget().getApplication().getCompetition().getId(), ((ApplicationFinanceRow) cost).getTarget().getOrganisation().getOrganisationType().getId());
                    return organisationFinanceHandler.toResource((ApplicationFinanceRow) cost);
                })
        );
    }

    private ServiceResult<FinanceRow> doUpdate(Long financeRowId, FinanceRowItem newCostItem) {
        return find(cost(financeRowId)).andOnSuccessReturn(existingCost -> {
            ApplicationFinance applicationFinance = existingCost.getTarget();
            OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getApplication().getCompetition().getId(), applicationFinance.getOrganisation().getOrganisationType().getId());
            ApplicationFinanceRow newCost = organisationFinanceHandler.toApplicationDomain(newCostItem);
            ApplicationFinanceRow updatedCost = mapCost(existingCost, newCost);

            ApplicationFinanceRow savedCost = organisationFinanceHandler.updateCost(updatedCost);

            newCost.getFinanceRowMetadata()
                    .stream()
                    .filter(c -> c.getValue() != null)
                    .filter(c -> !"null".equals(c.getValue()))
                    .forEach(costValue -> updateOrCreateCostValue(costValue, savedCost));

            // refresh the object, since we need to reload the costvalues, on the cost object.
            return savedCost;
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(final long financeRowId) {
        Application application = financeRowRepository.findById(financeRowId).get().getTarget().getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app -> {
            financeRowMetaValueRepository.deleteByFinanceRowId(financeRowId);
            financeRowRepository.deleteById(financeRowId);
            return serviceSuccess();
        });
    }

    /**
     * Get the cost handler by costItemId. This FinanceRowHandler can be used for validation or conversion of the FinanceRowItem.
     */
    @Override
    public FinanceRowHandler getCostHandler(long financeRowId) {
        return find(financeRowRepository.findById(financeRowId), notFoundError(ApplicationFinanceRow.class, financeRowId))
                .andOnSuccessReturn(row -> {
                    OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(row.getTarget().getApplication().getCompetition().getId(), row.getTarget().getOrganisation().getOrganisationType().getId());
                    return organisationFinanceHandler.getCostHandler(row.getType());
                }).getSuccess();
    }

    private FinanceRow addCostItem(ApplicationFinance applicationFinance, FinanceRowItem financeRowItem) {
        OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getApplication().getCompetition().getId(), applicationFinance.getOrganisation().getOrganisationType().getId());

        FinanceRow cost = organisationFinanceHandler.toApplicationDomain(financeRowItem);
        cost.setTarget(applicationFinance);
        cost.setType(financeRowItem.getCostType());

        return persistCostHandlingCostValues(cost, organisationFinanceHandler);
    }

    private ApplicationFinanceRow persistCostHandlingCostValues(FinanceRow cost, OrganisationTypeFinanceHandler financeHandler) {

        List<FinanceRowMetaValue> costValues = cost.getFinanceRowMetadata();
        cost.setFinanceRowMetadata(new ArrayList<>());
        ApplicationFinanceRow persistedCost = financeHandler.addCost((ApplicationFinanceRow) cost);
        costValues.stream().forEach(costVal -> costVal.setFinanceRowId(persistedCost.getId()));
        persistedCost.setFinanceRowMetadata(costValues);
        financeRowMetaValueRepository.saveAll(costValues);
        return financeHandler.updateCost(persistedCost);
    }

    private ApplicationFinanceRow mapCost(ApplicationFinanceRow currentCost, ApplicationFinanceRow newCost) {
        if (newCost.getCost() != null || costIsForOtherFunding(newCost) || costIsForAdditionalCosts(newCost)) {
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

    private boolean costIsForAdditionalCosts(ApplicationFinanceRow cost) {
        // respect null values for additional company costs costs, in order to produce correct validation messages
        return cost.getType() == FinanceRowType.ADDITIONAL_COMPANY_COSTS;
    }

    private boolean costIsForOtherFunding(ApplicationFinanceRow cost) {
        // respect null values for other funding costs, in order to produce correct validation messages
        return cost.getType() == FinanceRowType.OTHER_FUNDING;
    }

    private void updateOrCreateCostValue(FinanceRowMetaValue newMetaValue, FinanceRow savedCost) {
        if (newMetaValue.getFinanceRowMetaField() == null) {
            LOG.error("FinanceRowMetaField is null");
            return;
        }
        newMetaValue.setFinanceRowId(savedCost.getId());

        getMetaValueByFieldForFinanceRow(newMetaValue, savedCost)
                .andOnSuccessReturnVoid(currentMetaValue -> updateCostValue(currentMetaValue, newMetaValue))
                .andOnFailure(() -> {
                    createCostValue(newMetaValue, savedCost);
                    return serviceSuccess();
                });

    }

    private void updateCostValue(FinanceRowMetaValue currentMetaValue, FinanceRowMetaValue newMetaValue) {
        currentMetaValue.setValue(newMetaValue.getValue());
        financeRowMetaValueRepository.save(currentMetaValue);
    }

    private void createCostValue(FinanceRowMetaValue newMetaValue, FinanceRow savedCost) {
        Optional<FinanceRowMetaField> financeRowMetaField = financeRowMetaFieldRepository.findById(newMetaValue.getFinanceRowMetaField().getId());
        if (financeRowMetaField.isPresent()) {
            newMetaValue.setFinanceRowMetaField(financeRowMetaField.get());
        }
        newMetaValue = financeRowMetaValueRepository.save(newMetaValue);
        savedCost.addCostValues(newMetaValue);
    }

    private ServiceResult<FinanceRowMetaValue> getMetaValueByFieldForFinanceRow(FinanceRowMetaValue newMetaValue, FinanceRow savedCost) {
        return find(financeRowMetaValueRepository.financeRowIdAndFinanceRowMetaFieldId(savedCost.getId(), newMetaValue.getFinanceRowMetaField().getId()), notFoundError(FinanceRowMetaValue.class, newMetaValue.getId()));
    }

    private ServiceResult<ApplicationFinanceRow> getCost(Long costId) {
        return find(financeRowRepository.findById(costId), notFoundError(Question.class));
    }

    private Supplier<ServiceResult<ApplicationFinanceRow>> cost(Long costId) {
        return () -> getCost(costId);
    }


    private Supplier<ServiceResult<ApplicationFinance>> applicationFinance(Long applicationFinanceId) {
        return () -> getApplicationFinance(applicationFinanceId);
    }

    private ServiceResult<ApplicationFinance> getApplicationFinance(Long applicationFinanceId) {
        return find(applicationFinanceRepository.findById(applicationFinanceId), notFoundError(ApplicationFinance.class, applicationFinanceId));
    }

}