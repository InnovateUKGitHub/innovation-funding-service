package com.worth.ifs.application.finance.cost;

import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * {@code CostItem} interface is used to handle the different type of costItems
 * for an application.
 */
public interface CostItem {
    public Long getId();
    public Double getTotal();
}
