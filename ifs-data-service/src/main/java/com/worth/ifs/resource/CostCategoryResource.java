package com.worth.ifs.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.hateoas.ResourceSupport;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LabourCategoryResource.class, name = "labourcategoryresource"),
        @JsonSubTypes.Type(value = MaterialsCategoryResource.class, name = "materialscategoryresource") })
public interface CostCategoryResource  {
    public Double getTotal();
}
