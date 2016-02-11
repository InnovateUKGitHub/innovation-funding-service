package com.worth.ifs.finance.transactional;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.handler.item.OrganisationFinanceHandler;
import com.worth.ifs.finance.mapper.CostFieldMapper;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.repository.CostFieldRepository;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.repository.CostValueRepository;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.handlingErrors;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class CostServiceImpl implements CostService {

    private static final Log LOG = LogFactory.getLog(CostServiceImpl.class);

    @Autowired
    private CostFieldMapper costFieldMapper;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CostRepository costRepository;

    @Autowired
    private CostFieldRepository costFieldRepository;

    @Autowired
    private CostValueRepository costValueRepository;

    @Autowired
    private OrganisationFinanceHandler organisationFinanceHandler;

    @Override
    public ServiceResult<CostField> findOne(Long id) {
        return find(() -> costFieldRepository.findOne(id), notFoundError(CostField.class, id));
    }

    @Override
    public ServiceResult<List<CostFieldResource>> findAll() {
        List<CostField> allCostFields = costFieldRepository.findAll();
        List<CostFieldResource> resources = simpleMap(allCostFields, costFieldMapper::mapCostFieldToResource);
        return serviceSuccess(resources);
    }

    @Override
    public ServiceResult<CostItem> add(final Long applicationFinanceId, final Long questionId, final CostItem newCostItem) {

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
    public ServiceResult<Void> update(final Long id, final CostItem newCostItem) {
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
    public ServiceResult<List<Cost>> findByApplicationId(final Long applicationFinanceId) {
        return serviceSuccess(costRepository.findByApplicationFinanceId(applicationFinanceId));
    }

    @Override
    public ServiceResult<Cost> findById(final Long id) {
        return find(() -> costRepository.findOne(id), notFoundError(Cost.class, id));
    }

    @Override
    public ServiceResult<Void> delete(final Long costId) {
        return handlingErrors(() -> {
            costValueRepository.deleteByCostId(costId);
            costRepository.delete(costId);
            return serviceSuccess();
        });
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
}