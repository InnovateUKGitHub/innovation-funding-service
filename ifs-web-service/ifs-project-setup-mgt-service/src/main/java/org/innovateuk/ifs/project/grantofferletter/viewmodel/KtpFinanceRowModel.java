package org.innovateuk.ifs.project.grantofferletter.viewmodel;


public class KtpFinanceRowModel {

    private Integer cost;
    private Integer funding;

    public KtpFinanceRowModel(Integer cost, Integer funding) {
        this.cost = cost;
        this.funding = funding;
    }

    public Integer getCost() {
        return cost;
    }

    public Integer getFunding() {
        return funding;
    }

    public Integer getContribution() {
        return cost - funding;
    }
}
