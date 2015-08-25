package com.worth.ifs.assembler.mapping;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.resource.Materials;
import com.worth.ifs.resource.MaterialsCategoryResource;

import java.util.ArrayList;
import java.util.List;

public class MaterialsMapper implements ResourceMapper<Materials> {

    public MaterialsCategoryResource getCostResource(CostCategory costCategory) {
        List<Materials> materials = mapCosts(costCategory.getCosts());
        return new MaterialsCategoryResource(
                costCategory.getId(), materials, costCategory.getQuestion().getId(),
                costCategory.getQuestion().getSection().getId());
    }

    public List<Materials> mapCosts(List<Cost> costs) {
        List<Materials> materials = new ArrayList<>();
        costs.stream().forEach(c -> materials.add(new Materials(c.getId(),c.getItem(),c.getCost(),c.getQuantity())));
        return materials;
    }
}
