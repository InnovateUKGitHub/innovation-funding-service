package org.innovateuk.ifs.project.organisationdetails.edit.viewmodel;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.ApplicationYourOrganisationViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public class ProjectOrganisationSizeViewModel extends ApplicationYourOrganisationViewModel {

    private final ProjectResource project;
    private final String organisationName;
    private final long organisationId;
    private boolean readOnly;

    public ProjectOrganisationSizeViewModel(ProjectResource project,
                                            CompetitionResource competition,
                                            OrganisationResource organisation,
                                            boolean isMaximumFundingLevelConstant,
                                            boolean showOrganisationSizeAlert,
                                            boolean readOnly) {
        super(project.getApplication(), competition, organisation.getOrganisationTypeEnum(), isMaximumFundingLevelConstant, showOrganisationSizeAlert, true);
        this.project = project;
        this.organisationName = organisation.getName();
        this.organisationId = organisation.getId();
        this.readOnly = readOnly;
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

    public long getOrganisationId() {
        return organisationId;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectOrganisationSizeViewModel that = (ProjectOrganisationSizeViewModel) o;

        return new EqualsBuilder()
                .append(project, that.project)
                .append(organisationName, that.organisationName)
                .append(organisationId, that.organisationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(project)
                .append(organisationName)
                .append(organisationId)
                .toHashCode();
    }
}
