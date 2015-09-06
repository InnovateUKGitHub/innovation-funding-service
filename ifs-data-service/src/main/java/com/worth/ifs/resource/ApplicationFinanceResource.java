package com.worth.ifs.resource;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.worth.ifs.domain.Organisation;
import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class ApplicationFinanceResource extends ResourceSupport {

    Long applicationId;
    Organisation organisation;

    List<CostCategoryResource> costCategoryResources = new ArrayList<>();

    public Long getApplicationId() {
        return applicationId;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public List<CostCategoryResource> getCostCategoryResources() {
        return costCategoryResources;
    }

    public Double getTotal() {
        return costCategoryResources.stream().mapToDouble(c -> c!=null ? c.getTotal() : 0D).sum();
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void setCostCategoryResources(List<CostCategoryResource> costCategoryResources) {
        this.costCategoryResources = costCategoryResources;
    }
}
