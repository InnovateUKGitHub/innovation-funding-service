package com.worth.ifs.resource;

import java.util.ArrayList;
import java.util.List;

public class OverheadCategoryResource extends CostCategoryResource {
    Overhead overhead = new Overhead();
    Double total;

    public OverheadCategoryResource() {
    }

    public OverheadCategoryResource(Long sectionId, Long questionId, Long categoryId, Overhead overhead) {
        super(sectionId, questionId, categoryId);
        this.total = 0.0;
        this.overhead = overhead;
    }

    public Overhead getOverhead() {
        return overhead;
    }

    @Override
    public Double getTotal() {
        return total;
    }
}
