package org.innovateuk.ifs.project.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

/**
 * Class to hold the basic details such as project, application etc.
 */
public class BasicDetails {

    private ProjectResource project;
    private ApplicationResource application;
    private CompetitionResource competition;

    public BasicDetails(ProjectResource project, ApplicationResource application, CompetitionResource competition) {
        this.project = project;
        this.application = application;
        this.competition = competition;
    }

    public ProjectResource getProject() {
        return project;
    }

    public void setProject(ProjectResource project) {
        this.project = project;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionResource competition) {
        this.competition = competition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BasicDetails that = (BasicDetails) o;

        return new EqualsBuilder()
                .append(project, that.project)
                .append(application, that.application)
                .append(competition, that.competition)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(project)
                .append(application)
                .append(competition)
                .toHashCode();
    }
}
