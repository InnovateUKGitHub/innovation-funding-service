package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.SortedSet;

/**
 * Holder of model attributes for the acceptance of an application by an Assessor
 */
public class AssessmentAssignmentViewModel {

    private Long assessmentId;
    private Long competitionId;
    private ApplicationResource application;
    private SortedSet<OrganisationResource> partners;
    private OrganisationResource leadPartner;
    private String projectSummary;

    public AssessmentAssignmentViewModel(Long assessmentId, Long competitionId, ApplicationResource application, SortedSet<OrganisationResource> partners, OrganisationResource leadPartner, String projectSummary) {
        this.assessmentId = assessmentId;
        this.competitionId = competitionId;
        this.application = application;
        this.partners = partners;
        this.leadPartner = leadPartner;
        this.projectSummary = projectSummary;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public SortedSet<OrganisationResource> getPartners() {
        return partners;
    }

    public void setPartners(SortedSet<OrganisationResource> partners) {
        this.partners = partners;
    }

    public String getProjectSummary() {
        return projectSummary;
    }

    public void setProjectSummary(String projectSummary) {
        this.projectSummary = projectSummary;
    }

    public OrganisationResource getLeadPartner() {
        return leadPartner;
    }

    public void setLeadPartner(OrganisationResource leadPartner) {
        this.leadPartner = leadPartner;
    }
}
