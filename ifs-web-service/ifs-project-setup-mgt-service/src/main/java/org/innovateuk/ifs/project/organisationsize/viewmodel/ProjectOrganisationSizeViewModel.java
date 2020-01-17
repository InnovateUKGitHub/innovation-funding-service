package org.innovateuk.ifs.project.organisationsize.viewmodel;

import org.innovateuk.ifs.finance.resource.OrganisationSize;

import java.math.BigDecimal;
import java.util.List;

public class ProjectOrganisationSizeViewModel {

    private final long projectId;
    private final long competitionId;
    private final String projectName;
    private final String organisationName;
    private final List<OrganisationSize> organisationSizes;
    private final BigDecimal turnover;
    private final long employees;

    public ProjectOrganisationSizeViewModel(long projectId, long competitionId, String projectName, String organisationName, List<OrganisationSize> organisationSizes, BigDecimal turnover, long employees) {
        this.projectId = projectId;
        this.competitionId = competitionId;
        this.projectName = projectName;
        this.organisationName = organisationName;
        this.organisationSizes = organisationSizes;
        this.turnover = turnover;
        this.employees = employees;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public List<OrganisationSize> getOrganisationSizes() {
        return organisationSizes;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public long getEmployees() {
        return employees;
    }
}
