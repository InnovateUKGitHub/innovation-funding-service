package com.worth.ifs.finance.service;

import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.CostFieldResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link CostField} related data.
 */
public interface CostFieldRestService {
    public List<CostFieldResource> getCostFields();
}
