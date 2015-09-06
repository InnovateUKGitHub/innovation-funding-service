package com.worth.ifs.application.finance.cost;

import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

public interface CostItem {
    public Long getId();
    public Double getTotal();
}
