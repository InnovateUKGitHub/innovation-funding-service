package com.worth.ifs.assembler.mapping;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.resource.CostCategoryResource;

import java.util.List;

public interface ResourceMapper<T> {
    List<T> mapCosts(List<Cost> costs);
    CostCategoryResource getCostResource(CostCategory costCategory);
}
