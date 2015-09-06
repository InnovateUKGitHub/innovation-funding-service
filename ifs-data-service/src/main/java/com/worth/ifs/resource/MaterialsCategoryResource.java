package com.worth.ifs.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class MaterialsCategoryResource  extends CostCategoryResource {
    List<Materials> materials = new ArrayList<>();
    Double total;

    public MaterialsCategoryResource() {
    }

    public MaterialsCategoryResource(List<Materials> materials, Long questionId, Long sectionId) {
        super(sectionId, questionId);
        this.total = 0.0;
        this.materials = materials;

        calculateTotal();
    }

    public void calculateTotal() {
        total = materials.stream().mapToDouble(m -> m.getTotal()).sum();
    }

    public Double getTotal() {
        return total;
    }

    public List<Materials> getMaterials() {
        return materials;
    }
}
