package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.domain.ProcessActivity;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserRoleType;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private LocalDateTime submittedDate;
    private Boolean resubmission;
    private String previousApplicationNumber;
    private String previousApplicationTitle;
    @Min(0)
    private Long durationInMonths; // in months
    @Min(0)
    @Max(100)
    private BigDecimal completion = BigDecimal.ZERO;

    @OneToMany(mappedBy="application")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @OneToMany(mappedBy="application")
    private List<ApplicationFinance> applicationFinances = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="applicationStatusId", referencedColumnName="id")
    private ApplicationStatus applicationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="competition", referencedColumnName="id")
    private Competition competition;

    @OneToMany(mappedBy="application")
    private List<ApplicationInvite> invites;

    @Enumerated(EnumType.STRING)
    private FundingDecisionStatus fundingDecision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assessorFeedbackFileEntryId", referencedColumnName="id")
    private FileEntry assessorFeedbackFileEntry;
    
    @OneToMany(mappedBy="application", fetch=FetchType.LAZY, cascade={CascadeType.REMOVE, CascadeType.PERSIST})
    private List<FormInputResponse> formInputResponses = new ArrayList<>();

    private Boolean stateAidAgreed;

    public Application() {
        /*default constructor*/}

    public Application(Long id, String name, ApplicationStatus applicationStatus) {
        this.id = id;
        this.name = name;
        this.applicationStatus = applicationStatus;
    }

    public Application(Competition competition, String name, List<ProcessRole> processRoles, ApplicationStatus applicationStatus, Long id) {
        this.competition = competition;
        this.name = name;
        this.processRoles = processRoles;
        this.applicationStatus = applicationStatus;
        this.id = id;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Application;
    }

    public String getFormattedId(){
        return ApplicationResource.formatter.format(id);
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

    public void setResubmission(Boolean resubmission) { this.resubmission = resubmission; }

    public String getPreviousApplicationNumber() {
        return previousApplicationNumber;
    }

    public void setPreviousApplicationNumber(String previousApplicationNumber) { this.previousApplicationNumber = previousApplicationNumber; }

    public String getPreviousApplicationTitle() {
        return previousApplicationTitle;
    }

    public void setPreviousApplicationTitle(String previousApplicationTitle) { this.previousApplicationTitle = previousApplicationTitle; }


    public void setName(String name) {
        this.name = name;
    }

    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    public void setProcessRoles(List<ProcessRole> processRoles) {
        this.processRoles = processRoles;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public void addUserApplicationRole(ProcessRole... processRoles){
        if(this.processRoles == null){
            this.processRoles = new ArrayList<>();
        }
        this.processRoles.addAll(Arrays.asList(processRoles));
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @JsonIgnore
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

    @JsonIgnore
    public ProcessRole getLeadApplicantProcessRole(){
        return getLeadProcessRole().orElse(null);
    }

    @JsonIgnore
    private Optional<ProcessRole> getLeadProcessRole() {
        return this.processRoles.stream().filter(p -> UserRoleType.LEADAPPLICANT.getName().equals(p.getRole().getName())).findAny();
    }

    @JsonIgnore
    public User getLeadApplicant(){
        return getLeadProcessRole().map(role -> role.getUser()).orElse(null);
    }

    @JsonIgnore
    public Organisation getLeadOrganisation(){
        return getLeadProcessRole().map(role -> role.getOrganisation()).orElse(null);
    }

    @JsonIgnore
    public List<ApplicationInvite> getInvites() {
        return this.invites;
    }

    @JsonIgnore
    public boolean isOpen(){
        return Objects.equals(applicationStatus.getId(), ApplicationStatusConstants.OPEN.getId());
    }

    public void setInvites(List<ApplicationInvite> invites) {
        this.invites = invites;
    }

    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDateTime submittedDate) {
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
    
    public void addFormInputResponse(FormInputResponse formInputResponse) {
    	Optional<FormInputResponse> existing = getFormInputResponseByFormInput(formInputResponse.getFormInput());
    	if(existing.isPresent()) {
    		existing.get().setFileEntry(formInputResponse.getFileEntry());
    		existing.get().setUpdateDate(formInputResponse.getUpdateDate());
    		existing.get().setUpdatedBy(formInputResponse.getUpdatedBy());
    		existing.get().setValue(formInputResponse.getValue());
    	} else {
        	formInputResponses.add(formInputResponse);
    	}
    }

	public Optional<FormInputResponse> getFormInputResponseByFormInput(FormInput formInput) {
		return formInputResponses.stream().filter(fir -> formInput.equals(fir.getFormInput())).findFirst();
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
}
