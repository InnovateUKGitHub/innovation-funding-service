package com.worth.ifs.finance.controller;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.repository.CostFieldRepository;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.repository.CostValueRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cost")
public class CostController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    CostRepository costRepository;

    @Autowired
    CostFieldRepository costFieldRepository;

    @Autowired
    CostValueRepository costValueRepository;

    @RequestMapping("/add/{applicationFinanceId}/{questionId}")
    public void add(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId,
            @PathVariable("questionId") final Long questionId) {
        ApplicationFinance applicationFinance = applicationFinanceRepository.findOne(applicationFinanceId);
        Question question = questionRepository.findOne(questionId);
        Cost cost = new Cost("", "", 0, 0d, applicationFinance, question);
        costRepository.save(cost);
    }

    @RequestMapping("/update/{id}")
    public void update(@PathVariable("id") final Long id,
            @RequestBody final Cost newCost) {
        if(costRepository.exists(id)) {
            Cost updatedCost = mapCost(id, newCost);
            Cost savedCost = costRepository.save(updatedCost);

            for(CostValue costValue : newCost.getCostValues()) {
                if(costValue.getValue()!=null) {
                    CostField costField = costFieldRepository.findOne(costValue.getCostField().getId());
                    costValue.setCost(savedCost);
                    costValue.setCostField(costField);
                    costValueRepository.save(costValue);
                }
            }
        }
    }

    private Cost mapCost(Long id, Cost newCost) {
        Cost currentCost = costRepository.findOne(id);
        if(newCost.getCost()!=null) {
            currentCost.setCost(newCost.getCost());
        }
        if(newCost.getDescription()!=null) {
            currentCost.setDescription(newCost.getDescription());
        }
        if(newCost.getItem()!=null) {
            currentCost.setItem(newCost.getItem());
        }
        if(newCost.getQuantity()!=null) {
            currentCost.setQuantity(newCost.getQuantity());
        }

        return currentCost;
    }

    @RequestMapping("/get/{applicationFinanceId}")
    public List<Cost> findByApplicationId(@PathVariable("applicationFinanceId") final Long applicationFinanceId) {
        return costRepository.findByApplicationFinanceId(applicationFinanceId);
    }

    @RequestMapping("/findById/{id}")
    public Cost findById(@PathVariable("id") final Long id) {
        return costRepository.findOne(id);
    }

    @RequestMapping("/delete/{costId}")
    public void delete(@PathVariable("costId") final Long costId) {
        costValueRepository.deleteByCostId(costId);
        costRepository.delete(costId);
    }
}
