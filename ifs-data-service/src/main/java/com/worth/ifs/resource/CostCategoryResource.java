package com.worth.ifs.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.hateoas.ResourceSupport;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LabourCategoryResource.class, name = "labourcategoryresource"),
        @JsonSubTypes.Type(value = MaterialsCategoryResource.class, name = "materialscategoryresource"),
        @JsonSubTypes.Type(value = CapitalUsageCategoryResource.class, name = "capitalusagecategoryresource"),
        @JsonSubTypes.Type(value = TravelCostCategoryResource.class, name = "travelcostcategoryresource"),
        @JsonSubTypes.Type(value = SubContractingCostCategoryResource.class, name = "subcontractingcostcategoryresource"),
        @JsonSubTypes.Type(value = OtherCostCategoryResource.class, name = "othercostcategoryresource"),
        @JsonSubTypes.Type(value = OverheadCategoryResource.class, name = "overheadcategoryresource")})
abstract public class CostCategoryResource extends ResourceSupport {
    Long sectionId = 0L;
    Long questionId = 0L;
    Long categoryId = 0L;

    abstract public Double getTotal();

    public CostCategoryResource() {
    }

    public CostCategoryResource(Long sectionId, Long questionId, Long categoryId) {
        this.sectionId = sectionId;
        this.questionId = questionId;
        this.categoryId = categoryId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}
