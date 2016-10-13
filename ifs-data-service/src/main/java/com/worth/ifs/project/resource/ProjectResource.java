package com.worth.ifs.project.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.address.resource.AddressResource;

import javax.validation.constraints.Digits;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.application.resource.ApplicationResource.formatter;

public class ProjectResource {
    private static final int MAX_DURATION_IN_MONTHS_DIGITS = 2;

    private Long id;
    private Long application;
    private LocalDate targetStartDate;
    private AddressResource address;
    private String name;
    private LocalDateTime documentsSubmittedDate;
    private LocalDateTime offerSubmittedDate;
    private List<Long> projectUsers;
    private Long collaborationAgreement;
    private Long exploitationPlan;
    private Long signedGrantOfferLetter;
    private Long grantOfferLetter;
    private Long additionalContractFile;
    private boolean offerRejected;
    private Boolean otherDocumentsApproved;
    private LocalDateTime spendProfileSubmittedDate;

    @Digits(integer = MAX_DURATION_IN_MONTHS_DIGITS, fraction = 0, message="{validation.application.details.duration.in.months.max.digits}")
    private Long durationInMonths;

    @JsonIgnore
    public boolean isPartnerDocumentsSubmitted(){
        return documentsSubmittedDate != null;
    }

    @JsonIgnore
    public boolean isOfferSubmitted(){
        return offerSubmittedDate != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getTargetStartDate() {
        return targetStartDate;
    }

    public void setTargetStartDate(LocalDate targetStartDate) {
        this.targetStartDate = targetStartDate;
    }

    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
        this.address = address;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(List<Long> projectUsers) {
        this.projectUsers = projectUsers;
    }

    @JsonIgnore
    public String getFormattedId(){
        return formatter.format(id);
    }

    public Long getApplication() {
        return application;
    }

    public void setApplication(Long application) {
        this.application = application;
    }

    public LocalDateTime getDocumentsSubmittedDate() {
        return documentsSubmittedDate;
    }

    public void setDocumentsSubmittedDate(LocalDateTime documentsSubmittedDate) {
        this.documentsSubmittedDate = documentsSubmittedDate;
    }

    public LocalDateTime getOfferSubmittedDate() {
        return offerSubmittedDate;
    }

    public void setOfferSubmittedDate(LocalDateTime offerSubmittedDate) {
        this.offerSubmittedDate = offerSubmittedDate;
    }

    public Long getCollaborationAgreement() {
        return collaborationAgreement;
    }

    public void setCollaborationAgreement(Long collaborationAgreement) {
        this.collaborationAgreement = collaborationAgreement;
    }

    public Long getExploitationPlan() {
        return exploitationPlan;
    }

    public void setExploitationPlan(Long exploitationPlan) {
        this.exploitationPlan = exploitationPlan;
    }

    public Long getSignedGrantOfferLetter() {
        return signedGrantOfferLetter;
    }

    public void setSignedGrantOfferLetter(Long signedGrantOfferLetter) {
        this.signedGrantOfferLetter = signedGrantOfferLetter;
    }

    public Long getAdditionalContractFile() {
        return additionalContractFile;
    }

    public void setAdditionalContractFile(Long additionalContractFile) {
        this.additionalContractFile = additionalContractFile;
    }

    public boolean isOfferRejected() {
        return offerRejected;
    }

    public void setOfferRejected(boolean offerRejected) {
        this.offerRejected = offerRejected;
    }

    public Long getGrantOfferLetter() {
        return grantOfferLetter;
    }

    public void setGrantOfferLetter(Long grantOfferLetter) {
        this.grantOfferLetter = grantOfferLetter;
    }

    public Boolean getOtherDocumentsApproved() {
        return otherDocumentsApproved;
    }

    public void setOtherDocumentsApproved(Boolean otherDocumentsApproved) {
        this.otherDocumentsApproved = otherDocumentsApproved;
    }

    public LocalDateTime getSpendProfileSubmittedDate() {
        return spendProfileSubmittedDate;
    }

    public void setSpendProfileSubmittedDate(LocalDateTime spendProfileSubmittedDate) {
        this.spendProfileSubmittedDate = spendProfileSubmittedDate;
    }
}
