package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.CompanyAge;
import org.innovateuk.ifs.application.resource.CompanyPrimaryFocus;
import org.innovateuk.ifs.application.resource.CompetitionReferralSource;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.user.domain.ProcessActivity;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Application defines database relations and a model to use client side and server side.
 */
@Entity
public class Application implements ProcessActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate startDate;
    private ZonedDateTime submittedDate;
    private Boolean resubmission;
    private String previousApplicationNumber;
    private String previousApplicationTitle;
    private ZonedDateTime manageFundingEmailDate;

    @Min(0)
    private Long durationInMonths; // in months
    @Min(0)
    @Max(100)
    private BigDecimal completion = BigDecimal.ZERO;

    @OneToMany(mappedBy = "applicationId")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @OneToMany(mappedBy = "application")
    private List<ApplicationFinance> applicationFinances = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competition", referencedColumnName = "id")
    private Competition competition;

    @OneToMany(mappedBy = "application")
    private List<ApplicationInvite> invites;

    @Enumerated(EnumType.STRING)
    private FundingDecisionStatus fundingDecision;

    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<FormInputResponse> formInputResponses = new ArrayList<>();

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ApplicationResearchCategoryLink researchCategory;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ApplicationInnovationAreaLink innovationArea;

    @OneToOne(mappedBy = "target", cascade = CascadeType.ALL, optional=false, fetch = FetchType.LAZY)
    private ApplicationProcess applicationProcess;

    private boolean noInnovationAreaApplicable;

    private Boolean stateAidAgreed;

    private boolean inAssessmentReviewPanel;

    @Enumerated(EnumType.STRING)
    private CompetitionReferralSource competitionReferralSource;

    @Enumerated(EnumType.STRING)
    private CompanyAge companyAge;

    @Enumerated(EnumType.STRING)
    private CompanyPrimaryFocus companyPrimaryFocus;

    @OneToOne(mappedBy = "application", fetch = FetchType.LAZY)
    private Project project;

    public Application() {
    }

    public Application(String name) {
        this.name = name;
        this.applicationProcess = new ApplicationProcess(this, null, ApplicationState.CREATED);
    }

    public Application(Competition competition, String name, List<ProcessRole> processRoles) {
        this.competition = competition;
        this.name = name;
        this.processRoles = processRoles;
        this.applicationProcess = new ApplicationProcess(this, null, ApplicationState.CREATED);
    }

    protected boolean canEqual(Object other) {
        return other instanceof Application;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getResubmission() {
        return resubmission;
    }

    public void setResubmission(Boolean resubmission) {
        this.resubmission = resubmission;
    }

    public String getPreviousApplicationNumber() {
        return previousApplicationNumber;
    }

    public void setPreviousApplicationNumber(String previousApplicationNumber) {
        this.previousApplicationNumber = previousApplicationNumber;
    }

    public String getPreviousApplicationTitle() {
        return previousApplicationTitle;
    }

    public void setPreviousApplicationTitle(String previousApplicationTitle) {
        this.previousApplicationTitle = previousApplicationTitle;
    }

    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    public void setProcessRoles(List<ProcessRole> processRoles) {
        this.processRoles = processRoles;
    }

    public void removeProcessRoles(List<ProcessRole> processRolesToRemove) {
        processRoles.removeAll(processRolesToRemove);
    }

    public void addProcessRole(ProcessRole processRole) {
        processRoles.add(processRole);
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public void addUserApplicationRole(ProcessRole... processRoles) {
        if (this.processRoles == null) {
            this.processRoles = new ArrayList<>();
        }
        for (ProcessRole processRole : processRoles) {
            if (!this.processRoles.contains(processRole)) {
                this.processRoles.add(processRole);
            }
        }
    }

    public ZonedDateTime getManageFundingEmailDate() {
        return manageFundingEmailDate;
    }

    public void setManageFundingEmailDate(ZonedDateTime manageFundingEmailDate) {
        this.manageFundingEmailDate = manageFundingEmailDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public List<ApplicationFinance> getApplicationFinances() {
        return applicationFinances;
    }

    public void setApplicationFinances(List<ApplicationFinance> applicationFinances) {
        this.applicationFinances = applicationFinances;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public ProcessRole getLeadApplicantProcessRole() {
        return getLeadProcessRole().orElse(null);
    }

    private Optional<ProcessRole> getLeadProcessRole() {
        return this.processRoles.stream().filter(processRole -> processRole.getRole().isLeadApplicant()).findAny();
    }

    public User getLeadApplicant() {
        return getLeadProcessRole().map(ProcessRole::getUser).orElse(null);
    }

    public Long getLeadOrganisationId() {
        return getLeadProcessRole().map(ProcessRole::getOrganisationId).orElse(null);
    }

    public List<ApplicationInvite> getInvites() {
        return this.invites;
    }

    public void setInvites(List<ApplicationInvite> invites) {
        this.invites = invites;
    }

    public boolean isOpen() {
        return applicationProcess.isInState(ApplicationState.OPENED);
    }

    public boolean isSubmitted() {
        return applicationProcess.isInState(ApplicationState.SUBMITTED);
    }

    public ZonedDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(ZonedDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public FundingDecisionStatus getFundingDecision() {
        return fundingDecision;
    }

    public void setFundingDecision(FundingDecisionStatus fundingDecision) {
        this.fundingDecision = fundingDecision;
    }

    public List<FormInputResponse> getFormInputResponses() {
        return formInputResponses;
    }

    public void setFormInputResponses(List<FormInputResponse> formInputResponses) {
        this.formInputResponses = formInputResponses;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void addFormInputResponse(FormInputResponse formInputResponse, ProcessRole processRole) {
        Optional<FormInputResponse> existing = getFormInputResponseByFormInputAndProcessRole(formInputResponse.getFormInput(), processRole);
        if (existing.isPresent()) {
            existing.get().setFileEntry(formInputResponse.getFileEntry());
            existing.get().setUpdateDate(formInputResponse.getUpdateDate());
            existing.get().setUpdatedBy(formInputResponse.getUpdatedBy());
            existing.get().setValue(formInputResponse.getValue());
        } else {
            formInputResponses.add(formInputResponse);
        }
    }

    public Optional<FormInputResponse> getFormInputResponseByFormInputAndProcessRole(FormInput formInput, ProcessRole processRole) {
        if (formInput.getQuestion().getMultipleStatuses()) {
            return formInputResponses.stream().filter(fir -> formInput.equals(fir.getFormInput())
                    && fir.getUpdatedBy().getOrganisationId().equals(processRole.getOrganisationId())).findFirst();
        } else {
            return formInputResponses.stream().filter(fir -> formInput.equals(fir.getFormInput())).findFirst();
        }
    }

    public BigDecimal getCompletion() {
        return completion;
    }

    public void setCompletion(final BigDecimal completion) {
        this.completion = completion;
    }

    public ResearchCategory getResearchCategory() {
        if(researchCategory!=null) {
            return researchCategory.getCategory();
        }

        return null;
    }

    public void setResearchCategory(ResearchCategory newResearchCategory) {
        if (newResearchCategory == null) {
            researchCategory = null;
        }
        else {
            researchCategory = new ApplicationResearchCategoryLink(this, newResearchCategory);
        }
    }

    public InnovationArea getInnovationArea() {
        if(innovationArea!=null) {
            return innovationArea.getCategory();
        }

        return null;
    }

    public void setInnovationArea(InnovationArea newInnovationArea) {
        if (newInnovationArea == null) {
            innovationArea = null;
        }
        else {
            if (this.noInnovationAreaApplicable) {
                throw new IllegalStateException("InnovationArea not reconcilable with current value of noInnovationAreaApplies.");
            }
            innovationArea = new ApplicationInnovationAreaLink(this, newInnovationArea);
        }
    }

    public boolean getNoInnovationAreaApplicable()
    {
        return noInnovationAreaApplicable;
    }

    public void setNoInnovationAreaApplicable(boolean noInnovationAreaApplicable) {
        if (noInnovationAreaApplicable && innovationArea != null) {
            throw new IllegalStateException("noInnovationAreaApplicable cannot be set while an innovationArea is not null.");
        }

        this.noInnovationAreaApplicable = noInnovationAreaApplicable;
    }

    public ApplicationProcess getApplicationProcess() {
        return applicationProcess;
    }

    public boolean applicationFundingDecisionIsChangeable() {
        return !(this.manageFundingEmailDate != null &&
                (fundingDecision != null && fundingDecision.equals(FundingDecisionStatus.FUNDED)));
    }

    public boolean isInAssessmentReviewPanel() {
        return inAssessmentReviewPanel;
    }

    public void setInAssessmentReviewPanel(boolean inAssessmentReviewPanel) {
        this.inAssessmentReviewPanel = inAssessmentReviewPanel;
    }

    public boolean isCollaborativeProject() {
        CollaborationLevel collaborationLevel = competition.getCollaborationLevel();

        // A project is collaborative if the competition is collaborative or if there is more than a single
        // organisation when the competition supports collaboration

        if (collaborationLevel == null) {
            return false;
        }

        switch (collaborationLevel) {
            case SINGLE:
                return false;
            case COLLABORATIVE:
                return true;
            case SINGLE_OR_COLLABORATIVE: {
                long uniqueOrganisations = processRoles
                        .stream()
                        .filter(ProcessRole::isLeadApplicantOrCollaborator)
                        .map(ProcessRole::getOrganisationId)
                        .distinct()
                        .count();
                return uniqueOrganisations > 1;
            }
            default:
                throw new IllegalArgumentException("Unexpected enum constant: " + collaborationLevel);
        }
    }

    public CompetitionReferralSource getCompetitionReferralSource() {
        return competitionReferralSource;
    }

    public void setCompetitionReferralSource(CompetitionReferralSource competitionReferralSource) {
        this.competitionReferralSource = competitionReferralSource;
    }

    public CompanyAge getCompanyAge() {
        return companyAge;
    }

    public void setCompanyAge(CompanyAge companyAge) {
        this.companyAge = companyAge;
    }

    public CompanyPrimaryFocus getCompanyPrimaryFocus() {
        return companyPrimaryFocus;
    }

    public void setCompanyPrimaryFocus(CompanyPrimaryFocus companyPrimaryFocus) {
        this.companyPrimaryFocus = companyPrimaryFocus;
    }
}