package com.worth.ifs.finance.service;

import com.worth.ifs.finance.domain.Cost;
import java.util.List;

public interface CostRestService{
    public void add(Long applicationFinanceId, Long questionId);
    public List<Cost> getCosts(Long applicationFinanceId);
    public void update(Cost cost);
    public Cost findById(Long id);
    public void delete(Long costId);
}
