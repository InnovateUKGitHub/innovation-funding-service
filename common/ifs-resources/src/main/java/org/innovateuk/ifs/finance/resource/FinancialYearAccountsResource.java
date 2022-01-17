package org.innovateuk.ifs.finance.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmployeesAndTurnoverResource.class, name = "EmployeesAndTurnover"),
        @JsonSubTypes.Type(value = GrowthTableResource.class, name = "GrowthTable"),
        @JsonSubTypes.Type(value = KtpYearsResource.class, name = "KtpYears")
})
public abstract class FinancialYearAccountsResource {

    private Long employees;

    public Long getEmployees() {
        return employees;
    }

    public void setEmployees(Long employees) {
        this.employees = employees;
    }

    public abstract BigDecimal getTurnover();
}