package com.worth.ifs.project.financecheck.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FinanceCheckForm {
    private List<CostFormField> costs = new ArrayList<>();

    public FinanceCheckForm() {
    }

    public FinanceCheckForm(List<CostFormField> costs) {
        this.costs = costs;
    }

    public List<CostFormField> getCosts() {
        return costs;
    }

    public void setCosts(List<CostFormField> costs) {
        this.costs = costs;
    }

    public Optional<CostFormField> getCostFormByCategoryName(String categoryName){
        return costs.stream().filter(c -> c.getCostCategoryName().equals(categoryName)).findFirst();
    }

    public Long getTotalCost(){
        return costs.stream().mapToLong(c -> c.getValue().toBigInteger().longValue()).sum();
    }
}
