package com.worth.ifs.finance.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.handler.ApplicationFinanceHandler;
import com.worth.ifs.finance.handler.item.OrganisationFinanceHandler;
import com.worth.ifs.finance.mapper.CostFieldMapper;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.repository.CostFieldRepository;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.repository.CostValueRepository;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.handlingErrors;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class CostServiceImpl extends BaseTransactionalService implements CostService {

    private static final Log LOG = LogFactory.getLog(CostServiceImpl.class);

    @Autowired
    private CostFieldMapper costFieldMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CostRepository costRepository;

    @Autowired
    private CostFieldRepository costFieldRepository;

    @Autowired
    private CostValueRepository costValueRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Autowired
    private OrganisationFinanceHandler organisationFinanceHandler;



    @Override
    public ServiceResult<CostField> getCostFieldById(Long id) {
        return find(() -> costFieldRepository.findOne(id), notFoundError(CostField.class, id));
    }

    @Override
    public ServiceResult<List<CostFieldResource>> findAllCostFields() {
        List<CostField> allCostFields = costFieldRepository.findAll();
        List<CostFieldResource> resources = simpleMap(allCostFields, costFieldMapper::mapCostFieldToResource);
        return serviceSuccess(resources);
    }

    @Override
    public ServiceResult<CostItem> addCost(final Long applicationFinanceId, final Long questionId, final CostItem newCostItem) {

        return find(question(questionId), applicationFinance(applicationFinanceId)).andOnSuccess((question, applicationFinance) -> {

            if (newCostItem != null) {
                Cost newCost = addCostItem(applicationFinance, question, newCostItem);
                return serviceSuccess(organisationFinanceHandler.costToCostItem(newCost));
            } else {
                Cost cost = new Cost("", "", 0, BigDecimal.ZERO, applicationFinance, question);
                costRepository.save(cost);
                return serviceSuccess(organisationFinanceHandler.costToCostItem(cost));
            }
        });
    }

    @Override
    public ServiceResult<Void> updateCost(final Long id, final CostItem newCostItem) {
        return doUpdate(id, newCostItem).andOnSuccess(success -> serviceSuccess());
    }

    private ServiceResult<Cost> doUpdate(Long id, CostItem newCostItem) {
        return find(() -> costRepository.findOne(id), notFoundError(Cost.class, id)).andOnSuccess(existingCost -> {

            Cost newCost = organisationFinanceHandler.costItemToCost(newCostItem);
            Cost updatedCost = mapCost(existingCost, newCost);
            Cost savedCost = costRepository.save(updatedCost);

            newCost.getCostValues()
                    .stream()
                    .filter(c -> c.getValue() != null)
                    .filter(c -> !c.getValue().equals("null"))
                    .forEach(costValue -> updateCostValue(costValue, savedCost));

            return serviceSuccess(updatedCost);
        });
    }

    @Override
    public ServiceResult<List<Cost>> findCostsByApplicationId(final Long applicationFinanceId) {
        return serviceSuccess(costRepository.findByApplicationFinanceId(applicationFinanceId));
    }

    @Override
    public ServiceResult<Cost> findCostById(final Long id) {
        return find(() -> costRepository.findOne(id), notFoundError(Cost.class, id));
    }

    @Override
    public ServiceResult<Void> deleteCost(final Long costId) {
        return handlingErrors(() -> {
            costValueRepository.deleteByCostId(costId);
            costRepository.delete(costId);
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<ApplicationFinanceResource> findApplicationFinanceByApplicationIdAndOrganisation(Long applicationId, final Long organisationId) {
        return find(applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId), notFoundError(ApplicationFinance.class, applicationId, organisationId)).
                andOnSuccess(applicationFinance -> serviceSuccess(new ApplicationFinanceResource(applicationFinance)));
    }

    @Override
    public ServiceResult<List<ApplicationFinanceResource>> findApplicationFinanceByApplication(Long applicationId) {

        return find(applicationFinanceRepository.findByApplicationId(applicationId), notFoundError(ApplicationFinance.class, applicationId)).andOnSuccess(applicationFinances -> {

            List<ApplicationFinanceResource> applicationFinanceResources = new ArrayList<>();
            if (applicationFinances != null) {
                applicationFinances.stream().forEach(af -> applicationFinanceResources.add(new ApplicationFinanceResource(af)));
            }
            return serviceSuccess(applicationFinanceResources);
        });
    }

    @Override
    public ServiceResult<Double> getResearchParticipationPercentage(Long applicationId) {
        return getResearchPercentage(applicationId).andOnSuccess(percentage -> serviceSuccess(percentage.doubleValue()));
    }

    private ServiceResult<BigDecimal> getResearchPercentage(Long applicationId) {
        return find(applicationFinanceHandler.getResearchParticipationPercentage(applicationId), notFoundError(Application.class, applicationId));
    }

    @Override
    public ServiceResult<ApplicationFinanceResource> addCost(Long applicationId, Long organisationId) {

        return find(application(applicationId), organisation(organisationId)).andOnSuccess((application, organisation) -> {

            ApplicationFinance applicationFinance = new ApplicationFinance(application, organisation);

            applicationFinance = applicationFinanceRepository.save(applicationFinance);
            initialize(applicationFinance);
            return serviceSuccess(new ApplicationFinanceResource(applicationFinance));
        });
    }

    @Override
    public ServiceResult<ApplicationFinanceResource> getApplicationFinanceById(Long applicationFinanceId) {
        return find(applicationFinance(applicationFinanceId)).andOnSuccess(finance -> serviceSuccess(new ApplicationFinanceResource(finance)));
    }

    @Override
    public ServiceResult<ApplicationFinanceResource> updateCost(Long applicationFinanceId, ApplicationFinanceResource applicationFinance) {

        return find(applicationFinance(applicationFinanceId)).andOnSuccess(dbFinance -> {
            dbFinance.merge(applicationFinance);
            dbFinance = applicationFinanceRepository.save(dbFinance);
            return serviceSuccess(new ApplicationFinanceResource(dbFinance));
        });
    }

    @Override
    public ServiceResult<ApplicationFinanceResource> financeDetails(Long applicationId, Long organisationId) {
        ApplicationFinanceResourceId applicationFinanceResourceId = new ApplicationFinanceResourceId(applicationId, organisationId);
        return getApplicationFinanceForOrganisation(applicationId, organisationId, applicationFinanceResourceId);
    }

    @Override
    public ServiceResult<List<ApplicationFinanceResource>> financeTotals(Long applicationId) {
        return getFinanceTotals(applicationId);
    }

    private ServiceResult<List<ApplicationFinanceResource>> getFinanceTotals(Long applicationId) {
        return find(applicationFinanceHandler.getApplicationTotals(applicationId), notFoundError(ApplicationFinance.class, applicationId));
    }

    private ServiceResult<ApplicationFinanceResource> getApplicationFinanceForOrganisation(Long applicationId, Long organisationId, ApplicationFinanceResourceId applicationFinanceResourceId) {
        return find(applicationFinanceHandler.getApplicationOrganisationFinances(applicationFinanceResourceId), notFoundError(ApplicationFinance.class, applicationId, organisationId));
    }

    private Cost addCostItem(ApplicationFinance applicationFinance, Question question, CostItem newCostItem) {
        Cost existingCost = costRepository.findOneByApplicationFinanceIdAndQuestionId(applicationFinance.getId(), question.getId());
        if (existingCost == null) {
            Cost cost = organisationFinanceHandler.costItemToCost(newCostItem);
            cost.setQuestion(question);
            cost.setApplicationFinance(applicationFinance);
            return costRepository.save(cost);
        } else {
            ServiceResult<Cost> updated = doUpdate(existingCost.getId(), newCostItem);
            return updated.getSuccessObjectOrNull();
        }
    }

    private Cost mapCost(Cost currentCost, Cost newCost) {
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

    private void updateCostValue(CostValue costValue, Cost savedCost) {
        if (costValue.getCostField() == null) {
            LOG.error("CostField is null");
            return;
        }
        CostField costField = costFieldRepository.findOne(costValue.getCostField().getId());
        costValue.setCost(savedCost);
        costValue.setCostField(costField);
        costValueRepository.save(costValue);
    }


    private Supplier<ServiceResult<Question>> question(Long questionId) {
        return () -> getQuestion(questionId);
    }

    private ServiceResult<Question> getQuestion(Long questionId) {
        return find(questionRepository.findOne(questionId), notFoundError(Question.class));
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
        for (CostType costType : CostType.values()) {
            organisationFinanceHandler.initialiseCostType(applicationFinance, costType);
        }
    }
}