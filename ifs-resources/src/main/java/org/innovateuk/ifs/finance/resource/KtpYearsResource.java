package org.innovateuk.ifs.finance.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

public class KtpYearsResource extends FinancialYearAccountsResource {

    private List<KtpYearResource> years;

    private Long groupEmployees;

    public List<KtpYearResource> getYears() {
        return years;
    }

    public void setYears(List<KtpYearResource> years) {
        this.years = years;
    }

    public Long getGroupEmployees() {
        return groupEmployees;
    }

    public void setGroupEmployees(Long groupEmployees) {
        this.groupEmployees = groupEmployees;
    }

    @Override
    @JsonIgnore
    public Long getEmployees() {
        return years.stream().findFirst().map(KtpYearResource::getEmployees).orElse(null);
    }

    @Override
    public BigDecimal getTurnover() {
        return years.stream().findFirst().map(KtpYearResource::getTurnover).orElse(null);
    }
}
