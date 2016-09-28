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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FinanceCheckResource that = (FinanceCheckResource) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (project != null ? !project.equals(that.project) : that.project != null) return false;
        if (organisation != null ? !organisation.equals(that.organisation) : that.organisation != null) return false;
        return costGroup != null ? costGroup.equals(that.costGroup) : that.costGroup == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (project != null ? project.hashCode() : 0);
        result = 31 * result + (organisation != null ? organisation.hashCode() : 0);
        result = 31 * result + (costGroup != null ? costGroup.hashCode() : 0);
        return result;
    }

    public Long getOrganisation() {

        return organisation;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    @Override
    public String toString() {
        return "FinanceCheckResource{" +
                "id=" + id +
                ", project=" + project +
                ", organisation=" + organisation +
                ", costGroup=" + costGroup +
                '}';
    }
}
