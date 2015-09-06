package com.worth.ifs.controller;

import com.worth.ifs.domain.ApplicationFinance;
import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.Question;
import com.worth.ifs.repository.ApplicationFinanceRepository;
import com.worth.ifs.repository.CostRepository;
import com.worth.ifs.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cost")
public class CostController {

    @Autowired
    ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    CostRepository costRepository;

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
            @RequestParam final Cost updatedCost) {
        if(costRepository.exists(id)) {
            Cost currentCost = costRepository.findOne(id);
            currentCost.setCost(updatedCost.getCost());
            currentCost.setDescription(updatedCost.getDescription());
            currentCost.setItem(updatedCost.getItem());
            currentCost.setQuantity(updatedCost.getQuantity());
            costRepository.save(currentCost);
        }
    }

    @RequestMapping("/get/{applicationFinanceId}")
    public List<Cost> findByApplicationId(@PathVariable("applicationFinanceId") final Long applicationFinanceId) {
        return costRepository.findByApplicationFinanceId(applicationFinanceId);
    }
}
