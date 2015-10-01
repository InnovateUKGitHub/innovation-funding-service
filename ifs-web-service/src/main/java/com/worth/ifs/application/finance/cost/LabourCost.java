package com.worth.ifs.application.finance.cost;

import com.worth.ifs.application.finance.CostType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

public class LabourCost implements CostItem {
    private final Log log = LogFactory.getLog(getClass());

    private Long id;
    private String role;
    private Double grossAnnualSalary;
    private Integer labourDays;
    private Double rate;
    private String description;
    private Double total;

    public LabourCost() {
    }

    public LabourCost(Long id, String role, Double grossAnnualSalary, Integer labourDays, String description) {
        this.id = id;
        this.role = role;
        this.grossAnnualSalary = grossAnnualSalary;
        this.labourDays = labourDays;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public Double getGrossAnnualSalary() {
        return grossAnnualSalary;
    }

    public Double getRatePerDay(Integer workingDaysPerYear) {
        if(workingDaysPerYear.equals(0)) {
            return 0D;
        }
        return grossAnnualSalary / workingDaysPerYear.doubleValue();
    }

    public Double getRate(Integer workingDaysPerYear) {

        if(grossAnnualSalary!=null && workingDaysPerYear != null) {
            rate = getRatePerDay(workingDaysPerYear);
            return rate;
        }
        return rate;
    }

    public Double getRate() {
        return rate;
    }

    public Integer getLabourDays() {
        return labourDays;
    }

    public String getDescription() {
        return description;
    }

    public Double getTotal() {
        return total;
    }

    public Double getTotal(Integer workingDaysPerYear) {
        getRate(workingDaysPerYear);
        calculateTotal();
        return total;
    }

    private void calculateTotal() {
        if(rate!=null && !rate.equals(Double.NaN) && labourDays!=null) {
            total = rate * labourDays;
        } else {
            total = 0D;
        }
    }

    public void setGrossAnnualSalary(Double grossAnnualSalary) {
        this.grossAnnualSalary = grossAnnualSalary;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setLabourDays(Integer labourDays) {
        this.labourDays = labourDays;
    }
}
