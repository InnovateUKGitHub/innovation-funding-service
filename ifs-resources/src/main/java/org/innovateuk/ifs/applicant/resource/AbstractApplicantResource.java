package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by luke.harper on 25/04/2017.
 */
public abstract class AbstractApplicantResource {

    private ApplicationResource application;

    private CompetitionResource competition;

    private ApplicantResource currentApplicant;

    private List<ApplicantResource> applicants = new ArrayList<>();

    private List<ProcessRoleResource> assignableProcessRoles = new ArrayList<>();

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

    public ApplicantResource getCurrentApplicant() {
        return currentApplicant;
    }

    public void setCurrentApplicant(ApplicantResource user) {
        this.currentApplicant = user;
    }

    public void addApplicant(ApplicantResource applicant) {
        this.applicants.add(applicant);
    }

    public List<ApplicantResource> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<ApplicantResource> applicants) {
        this.applicants = applicants;
    }

    public List<ProcessRoleResource> getAssignableProcessRoles() {
        return assignableProcessRoles;
    }

    public void setAssignableProcessRoles(List<ProcessRoleResource> assignableProcessRoles) {
        this.assignableProcessRoles = assignableProcessRoles;
    }

    public Stream<OrganisationResource> allOrganisations() {
        return applicants.stream().map(ApplicantResource::getOrganisation);
    }
}
