package org.innovateuk.ifs.project.organisationdetails.edit.viewmodel;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public class ProjectOrganisationSizeViewModel {

    private final ProjectResource project;
    private final String organisationName;
    private final long organisationId;
    private boolean showStateAidAgreement;
    private boolean showOrganisationSizeAlert;
    private boolean h2020;
    private boolean readOnly;
    private boolean procurementCompetition;

    public ProjectOrganisationSizeViewModel(ProjectResource project, String organisationName,
                                            long organisationId,
                                            boolean showStateAidAgreement,
                                            boolean showOrganisationSizeAlert,
                                            boolean h2020,
                                            boolean readOnly,
                                            boolean procurementCompetition) {
        this.project = project;
        this.organisationName = organisationName;
        this.organisationId = organisationId;
        this.showStateAidAgreement = showStateAidAgreement;
        this.showOrganisationSizeAlert = showOrganisationSizeAlert;
        this.h2020 = h2020;
        this.readOnly = readOnly;
        this.procurementCompetition = procurementCompetition;
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

    public boolean isShowStateAidAgreement() {
        return showStateAidAgreement;
    }

    public boolean isShowOrganisationSizeAlert() {
        return showOrganisationSizeAlert;
    }

    public boolean isH2020() {
        return h2020;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isProcurementCompetition() {
        return procurementCompetition;
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
                .append(showStateAidAgreement, that.showStateAidAgreement)
                .append(showOrganisationSizeAlert, that.showOrganisationSizeAlert)
                .append(h2020, that.h2020)
                .append(procurementCompetition, that.procurementCompetition)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(project)
                .append(organisationName)
                .append(organisationId)
                .append(showStateAidAgreement)
                .append(showOrganisationSizeAlert)
                .append(h2020)
                .append(procurementCompetition)
                .toHashCode();
    }
}
