package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

/**
 * Created by luke.harper on 25/04/2017.
 */
public abstract class AbstractApplicantResource {

    private ApplicationResource application;

    private CompetitionResource competition;

    private ApplicantResource user;

    private List<ApplicantResource> applicants;

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

    public ApplicantResource getUser() {
        return user;
    }

    public void setUser(ApplicantResource user) {
        this.user = user;
    }

    public List<ApplicantResource> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<ApplicantResource> applicants) {
        this.applicants = applicants;
    }
}
