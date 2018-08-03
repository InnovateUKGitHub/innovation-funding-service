package org.innovateuk.ifs.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;

import java.util.ArrayList;
import java.util.List;

public class AssessorProfileDeclarationViewModel {

    private CompetitionResource competition;
    private AssessorProfileDetailsViewModel assessorProfileDetailsViewModel;
    private boolean completed;
    private String principalEmployer;
    private String role;
    private String professionalAffiliations;
    private List<AffiliationResource> appointments = new ArrayList<>();
    private String financialInterests;
    private List<AffiliationResource> familyAffiliations = new ArrayList<>();
    private String familyFinancialInterests;
    private String originQuery;
    private boolean compAdminUser;

    public AssessorProfileDeclarationViewModel(CompetitionResource competition,
                                               AssessorProfileDetailsViewModel assessorProfileDetailsViewModel,
                                               boolean completed,
                                               String principalEmployer,
                                               String role,
                                               String professionalAffiliations,
                                               List<AffiliationResource> appointments,
                                               String financialInterests,
                                               List<AffiliationResource> familyAffiliations,
                                               String familyFinancialInterests,
                                               String originQuery,
                                               boolean compAdminUser) {
        this.competition = competition;
        this.assessorProfileDetailsViewModel = assessorProfileDetailsViewModel;
        this.completed = completed;
        this.principalEmployer = principalEmployer;
        this.role = role;
        this.professionalAffiliations = professionalAffiliations;
        this.appointments = appointments;
        this.financialInterests = financialInterests;
        this.familyAffiliations = familyAffiliations;
        this.familyFinancialInterests = familyFinancialInterests;
        this.originQuery = originQuery;
        this.compAdminUser = compAdminUser;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public AssessorProfileDetailsViewModel getAssessorProfileDetailsViewModel() {
        return assessorProfileDetailsViewModel;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getPrincipalEmployer() {
        return principalEmployer;
    }

    public String getRole() {
        return role;
    }

    public String getProfessionalAffiliations() {
        return professionalAffiliations;
    }

    public List<AffiliationResource> getAppointments() {
        return appointments;
    }

    public String getFinancialInterests() {
        return financialInterests;
    }

    public List<AffiliationResource> getFamilyAffiliations() {
        return familyAffiliations;
    }

    public String getFamilyFinancialInterests() {
        return familyFinancialInterests;
    }

    public String getOriginQuery() {
        return originQuery;
    }

    public boolean isCompAdminUser() {
        return compAdminUser;
    }
}
