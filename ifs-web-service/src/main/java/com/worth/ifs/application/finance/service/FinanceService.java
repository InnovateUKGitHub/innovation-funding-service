package com.worth.ifs.application.finance.service;

import com.worth.ifs.application.finance.CostCategory;
import com.worth.ifs.application.finance.CostType;
import com.worth.ifs.finance.domain.ApplicationFinance;

import java.util.EnumMap;
import java.util.List;

public interface FinanceService {

    public EnumMap<CostType, CostCategory> getFinances(Long applicationFinanceId);
    public Double getTotal(Long applicationFinanceId);
    public ApplicationFinance addApplicationFinance(Long applicationId, Long userId);
    public ApplicationFinance getApplicationFinance(Long applicationId, Long userId);
    public List<ApplicationFinance> getApplicationFinances(Long applicationId);
    public void addCost(Long applicationFinanceId , Long questionId);
}
