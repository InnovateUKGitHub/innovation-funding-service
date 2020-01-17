package org.innovateuk.ifs.project.organisationsize.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public class ProjectOrganisationSizeViewModel {

    private final ProjectResource project;
    private final String organisationName;
    private final OrganisationSize organisationSize;
    private final BigDecimal turnover;
    private final long employees;

    public ProjectOrganisationSizeViewModel(ProjectResource project, String organisationName, OrganisationSize organisationSize, BigDecimal turnover, long employees) {
        this.project = project;
        this.organisationName = organisationName;
        this.organisationSize = organisationSize;
        this.turnover = turnover;
        this.employees = employees;
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

    public long getEmployees() {
        return employees;
    }
}
