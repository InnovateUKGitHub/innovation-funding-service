package com.worth.ifs.finance.service;

import com.worth.ifs.finance.domain.CostField;

import java.util.List;

/**
 * Interface for CRUD operations on {@link CostField} related data.
 */
public interface CostFieldRestService {
    public List<CostField> getCostFields();
}
