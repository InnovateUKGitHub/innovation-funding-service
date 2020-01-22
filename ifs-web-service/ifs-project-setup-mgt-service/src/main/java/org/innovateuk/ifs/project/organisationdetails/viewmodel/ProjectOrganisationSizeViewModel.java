package org.innovateuk.ifs.project.organisationdetails.viewmodel;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;


import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.project.resource.ProjectResource;

public class ProjectOrganisationSizeViewModel {

    private final ProjectResource project;
    private final String organisationName;
    private final OrganisationSize organisationSize;
    private final BigDecimal turnover;
    private final Long employees;
    private final boolean showStateAidAgreement;
    private final boolean fundingSectionComplete;
    private final boolean h2020;
    private final boolean readOnly;
    private final boolean showHints;

    public ProjectOrganisationSizeViewModel(ProjectResource project, String organisationName, OrganisationSize organisationSize, BigDecimal turnover, Long employees, boolean showStateAidAgreement, boolean fundingSectionComplete, boolean h2020, boolean readOnly, boolean showHints) {
        this.project = project;
        this.organisationName = organisationName;
        this.organisationSize = organisationSize;
        this.turnover = turnover;
        this.employees = employees;
        this.showStateAidAgreement = showStateAidAgreement;
        this.fundingSectionComplete = fundingSectionComplete;
        this.h2020 = h2020;
        this.readOnly = readOnly;
        this.showHints = showHints;
    }

    public List<FormOption> getOrganisationSizeOptions() {
        return simpleMap(OrganisationSize.values(), size -> new FormOption(size.getDescription(), size.name()));
    }

    public ProjectResource getProject() {
        return project;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public Long getEmployees() {
        return employees;
    }

    public boolean isShowStateAidAgreement() {
        return showStateAidAgreement;
    }

    public boolean isShowOrganisationSizeAlert() {
        return fundingSectionComplete;
    }

    public boolean isH2020() {
        return h2020;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isShowHints() {
        return showHints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectOrganisationSizeViewModel that = (ProjectOrganisationSizeViewModel) o;

        return new EqualsBuilder()
                .append(project, that.project)
                .append(organisationName, that.organisationName)
                .append(organisationSize, that.organisationSize)
                .append(turnover, that.turnover)
                .append(employees, that.employees)
                .append(showStateAidAgreement, that.showStateAidAgreement)
                .append(fundingSectionComplete, that.fundingSectionComplete)
                .append(h2020, that.h2020)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(project)
                .append(organisationName)
                .append(organisationSize)
                .append(turnover)
                .append(employees)
                .append(showStateAidAgreement)
                .append(fundingSectionComplete)
                .append(h2020)
                .toHashCode();
    }
}
