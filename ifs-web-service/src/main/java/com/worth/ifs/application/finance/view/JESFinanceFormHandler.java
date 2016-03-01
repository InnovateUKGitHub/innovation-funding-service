package com.worth.ifs.application.finance.view;

import com.worth.ifs.finance.resource.cost.CostItem;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class JESFinanceFormHandler implements FinanceFormHandler {

    @Override
    public void update(HttpServletRequest request, Long userId, Long applicationId) {

    }

    @Override
    public void storeCost(Long userId, Long applicationId, String fieldName, String value) {

    }

    @Override
    public void updateFinancePosition(Long userId, Long applicationId, String fieldName, String value) {

    }

    @Override
    public CostItem addCost(Long applicationId, Long userId, Long questionId) {
        return null;
    }
}
