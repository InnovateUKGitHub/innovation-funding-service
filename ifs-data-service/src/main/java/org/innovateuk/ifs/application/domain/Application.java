package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.category.domain.ApplicationInnovationAreaLink;
import org.innovateuk.ifs.category.domain.ApplicationResearchCategoryLink;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.ProcessActivity;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityState;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Application defines database relations and a model to use client side and server side.
 */
@Entity
public class Application implements ProcessActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition", referencedColumnName = "id")
    private Competition competition;

    @OneToMany(mappedBy = "application")
    private List<ApplicationInvite> invites;

    @Enumerated(EnumType.STRING)
    private FundingDecisionStatus fundingDecision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessorFeedbackFileEntryId", referencedColumnName = "id")
    private FileEntry assessorFeedbackFileEntry;

    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<FormInputResponse> formInputResponses = new ArrayList<>();

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApplicationResearchCategoryLink researchCategory;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApplicationInnovationAreaLink innovationArea;

    @OneToOne(mappedBy = "target", cascade = CascadeType.ALL, optional=false)
    private ApplicationProcess applicationProcess;

    private boolean noInnovationAreaApplicable;

    private Boolean stateAidAgreed;

    public Application() {
    }

    public Application(String name, ActivityState activityState) {
        requireNonNull(activityState, "activityState cannot be null " + activityState);
        this.name = name;
        this.applicationProcess = new ApplicationProcess(this, null, activityState);
    }

    public Application(Competition competition, String name, List<ProcessRole> processRoles, ActivityState activityState) {
        requireNonNull(activityState, "activityState cannot be null " + activityState);
        this.competition = competition;
        this.name = name;
        this.processRoles = processRoles;
        this.applicationProcess = new ApplicationProcess(this, null, activityState);
    }

    protected boolean canEqual(Object other) {
        return other instanceof Application;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
    }

    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    public void setProcessRoles(List<ProcessRole> processRoles) {
        this.processRoles = processRoles;
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

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public void setApplicationFinances(List<ApplicationFinance> applicationFinances) {
        this.applicationFinances = applicationFinances;
    }

    public ProcessRole getLeadApplicantProcessRole() {
        return getLeadProcessRole().orElse(null);
    }

    private Optional<ProcessRole> getLeadProcessRole() {
        return this.processRoles.stream().filter(p -> UserRoleType.LEADAPPLICANT.getName().equals(p.getRole().getName())).findAny();
    }

    public User getLeadApplicant() {
        return getLeadProcessRole().map(role -> role.getUser()).orElse(null);
    }

    public Long getLeadOrganisationId() {
        return getLeadProcessRole().map(role -> role.getOrganisationId()).orElse(null);
    }

    public List<ApplicationInvite> getInvites() {
        return this.invites;
    }

    public boolean isOpen() {
        return applicationProcess.isInState(ApplicationState.OPEN);
    }

    public void setInvites(List<ApplicationInvite> invites) {
        this.invites = invites;
    }

    public ZonedDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(ZonedDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public void setFundingDecision(FundingDecisionStatus fundingDecision) {
        this.fundingDecision = fundingDecision;
    }

    public FundingDecisionStatus getFundingDecision() {
        return fundingDecision;
    }

    public FileEntry getAssessorFeedbackFileEntry() {
        return assessorFeedbackFileEntry;
    }

    public void setAssessorFeedbackFileEntry(FileEntry assessorFeedbackFileEntry) {
        this.assessorFeedbackFileEntry = assessorFeedbackFileEntry;
    }

    public List<FormInputResponse> getFormInputResponses() {
        return formInputResponses;
    }

    public void setFormInputResponses(List<FormInputResponse> formInputResponses) {
        this.formInputResponses = formInputResponses;
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

    public Boolean getStateAidAgreed() {
        return stateAidAgreed;
    }

    public void setStateAidAgreed(Boolean stateAidAgreed) {
        this.stateAidAgreed = stateAidAgreed;
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
}
