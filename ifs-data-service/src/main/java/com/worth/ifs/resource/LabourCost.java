package com.worth.ifs.resource;

import com.worth.ifs.domain.Cost;
import org.springframework.hateoas.Resource;

public class LabourCost {

    private Long id;
    private String role;
    private Double grossAnnualSalary;
    private Integer labourDays;
    private Double rate;
    private Double total;

    public LabourCost() {
    }

    public LabourCost(Long id, String role, Double grossAnnualSalary, Integer labourDays) {
        this.id = id;
        this.role = role;
        this.grossAnnualSalary = grossAnnualSalary;
        this.labourDays = labourDays;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public Double getGrossAnnualSalary() {
        if(grossAnnualSalary!=null) {
            return grossAnnualSalary;
        }
        else {
            return 0D;
        }
    }

    public Double getRate(Integer workingDaysPerYear) {
        if(grossAnnualSalary!=null && workingDaysPerYear != null) {
            rate = grossAnnualSalary / workingDaysPerYear.doubleValue();
            return rate;
        } else {
            return 0D;
        }
    }

    public Double getRate() {
        return rate;
    }

    public Integer getLabourDays() {
        return labourDays;
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
        if(rate!=null && labourDays!=null) {
            total = rate * labourDays;
        } else {
            total = 0D;
        }
    }

}
