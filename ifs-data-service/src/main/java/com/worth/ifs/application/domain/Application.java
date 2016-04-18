package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Application defines database relations and a model to use client side and server side.
 */
@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private LocalDate startDate;
    private LocalDateTime submittedDate;
    @Min(0)
    private Long durationInMonths; // in months

    @OneToMany(mappedBy="application", fetch = FetchType.EAGER)
    private List<ProcessRole> processRoles = new ArrayList<>();

    @OneToMany(mappedBy="application")
    private List<ApplicationFinance> applicationFinances = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="applicationStatusId", referencedColumnName="id")
    private ApplicationStatus applicationStatus;

    @ManyToOne
    @JoinColumn(name="competition", referencedColumnName="id")
    private Competition competition;

    @OneToMany(mappedBy="application")
    private List<Invite> invites;


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
        Optional<ProcessRole> role = this.processRoles.stream().filter(p -> UserRoleType.LEADAPPLICANT.getName().equals(p.getRole().getName())).findAny();
        if(role.isPresent()){
            return role.get();
        }
        return null;
    }

    @JsonIgnore
    public User getLeadApplicant(){
        Optional<ProcessRole> role = this.processRoles.stream().filter(p -> UserRoleType.LEADAPPLICANT.getName().equals(p.getRole().getName())).findAny();
        if(role.isPresent()){
            return role.get().getUser();
        }
        return null;
    }

    @JsonIgnore
    public Organisation getLeadOrganisation(){
        Optional<ProcessRole> role = this.processRoles.stream().filter(p -> UserRoleType.LEADAPPLICANT.getName().equals(p.getRole().getName())).findAny();
        if(role.isPresent()){
            return role.get().getOrganisation();
        }
        return null;
    }

    @JsonIgnore
    public List<Invite> getInvites() {
        return this.invites;
    }

    @JsonIgnore
    public boolean isOpen(){
        return Objects.equals(applicationStatus.getId(), ApplicationStatusConstants.OPEN.getId());
    }

    public void setInvites(List<Invite> invites) {
        this.invites = invites;
    }

    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }
}
