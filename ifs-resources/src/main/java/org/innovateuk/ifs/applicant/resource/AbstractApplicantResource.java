package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Abstract class for all rich applicant resources.
 */
public abstract class AbstractApplicantResource {

    private ApplicationResource application;

    private CompetitionResource competition;

    private ApplicantResource currentApplicant;

    private UserResource currentUser;

    private List<ApplicantResource> applicants = new ArrayList<>();

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

    public UserResource getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserResource currentUser) {
        this.currentUser = currentUser;
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

    public Stream<OrganisationResource> allOrganisations() {
        return applicants.stream().map(ApplicantResource::getOrganisation);
    }
}
