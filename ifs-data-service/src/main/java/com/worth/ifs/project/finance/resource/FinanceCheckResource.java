package com.worth.ifs.project.finance.resource;

public class FinanceCheckResource {
    private Long id;
    private Long project;
    private Long organisation;
    private CostGroupResource costGroup = new CostGroupResource();

    public FinanceCheckResource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }

    public CostGroupResource getCostGroup() {
        return costGroup;
    }

    public void setCostGroup(CostGroupResource costGroup) {
        this.costGroup = costGroup;
    }

    public Long getOrganisation() {

        return organisation;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }
}
