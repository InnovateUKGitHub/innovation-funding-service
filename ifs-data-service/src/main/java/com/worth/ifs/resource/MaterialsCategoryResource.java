package com.worth.ifs.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class MaterialsCategoryResource  extends ResourceSupport implements CostCategoryResource {
    Long sectionId;
    Long questionId;
    Long categoryId;

    List<Materials> materials = new ArrayList<>();
    Double total;

    public MaterialsCategoryResource() {
    }

    public MaterialsCategoryResource(Long categoryId, List<Materials> materials, Long questionId, Long sectionId) {
        this.categoryId = categoryId;
        this.total = 0.0;
        this.questionId = questionId;
        this.sectionId = sectionId;
        this.materials = materials;

        calculateTotal();
    }

    public void calculateTotal() {
        total = materials.stream().mapToDouble(m -> m.getTotal()).sum();
    }

    public Double getTotal() {
        return total;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public List<Materials> getMaterials() {
        return materials;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}
