package com.worth.ifs.project.domain;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.invite.domain.ProcessActivity;
import com.worth.ifs.invite.domain.ProjectParticipantRole;
import com.worth.ifs.user.domain.Organisation;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static java.util.stream.Collectors.toList;

/**
 *  A project represents an application that has been accepted (and is now in project setup phase).
 *  It stores details specific to project (which are different from application)
 */
@Entity
public class Project implements ProcessActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name="applicationId", referencedColumnName = "id")
    private Application application;

    private LocalDate targetStartDate;

    @OneToOne (cascade = CascadeType.ALL)
    @JoinColumn(name="address", referencedColumnName="id")
    private Address address;

    @Min(0)
    private Long durationInMonths; // in months

    private String name;

    private LocalDateTime documentsSubmittedDate;

    private LocalDateTime offerSubmittedDate;

    @OneToMany(mappedBy="project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectUser> projectUsers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="collaborationAgreementFileEntryId", referencedColumnName="id")
    private FileEntry collaborationAgreement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="exploitationPlanFileEntryId", referencedColumnName="id")
    private FileEntry exploitationPlan;

    @OneToOne
    @JoinColumn(name="signedGrantOfferFileEntryId", referencedColumnName = "id")
    private FileEntry signedGrantOfferLetter;

    @OneToOne
    @JoinColumn(name="grantOfferLetterFileEntryId", referencedColumnName = "id")
    private FileEntry grantOfferLetter;

    @OneToOne
    @JoinColumn(name="additionalContractFileEntryId", referencedColumnName = "id")
    private FileEntry additionalContractFile;

    @NotNull
    private boolean offerRejected;

    private Boolean otherDocumentsApproved;


    public Project() {}

    public Project(Long id, Application application, LocalDate targetStartDate, Address address,

                   Long durationInMonths, String name, LocalDateTime documentsSubmittedDate) {

        this.id = id;
        this.application = application;
        this.targetStartDate = targetStartDate;
        this.address = address;
        this.durationInMonths = durationInMonths;
        this.name = name;
        this.documentsSubmittedDate = documentsSubmittedDate;
    }

    public void addProjectUser(ProjectUser projectUser) {
        projectUsers.add(projectUser);
    }

    public boolean removeProjectUser(ProjectUser projectUser) {
        return projectUsers.remove(projectUser);
    }

    public ProjectUser getExistingProjectUserWithRoleForOrganisation(ProjectParticipantRole role, Organisation organisation) {
        List<ProjectUser> matchingUser = simpleFilter(projectUsers, projectUser -> projectUser.getRole()==role && projectUser.getOrganisation().equals(organisation));

        if (matchingUser.isEmpty()) {
            return null;
        }

        return getOnlyElement(matchingUser);
    }

    public Long getId() {
        return id;
    }

    public String getFormattedId() {
        return ApplicationResource.formatter.format(id);
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
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

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public List<ProjectUser> getProjectUsers() {
        return projectUsers;
    }

    public List<ProjectUser> getProjectUsers(Predicate<ProjectUser> filter){
        return projectUsers.stream().filter(filter).collect(toList());
    }

    public List<ProjectUser> getProjectUsersWithRole(ProjectParticipantRole... roles){
        return getProjectUsers(pu -> Arrays.stream(roles).anyMatch(pu.getRole()::equals));
    }

    public List<Organisation> getOrganisations(){
        return projectUsers.stream().map(pu -> pu.getOrganisation()).distinct().collect(toList());
    }

    public List<Organisation> getOrganisations(Predicate<Organisation> predicate){
        return getOrganisations().stream().filter(predicate).collect(toList());
    }

    public void setProjectUsers(List<ProjectUser> projectUsers) {
        this.projectUsers.clear();
        this.projectUsers.addAll(projectUsers);
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

    public FileEntry getCollaborationAgreement() {
        return collaborationAgreement;
    }

    public void setCollaborationAgreement(FileEntry collaborationAgreement) {
        this.collaborationAgreement = collaborationAgreement;
    }

    public FileEntry getExploitationPlan() {
        return exploitationPlan;
    }

    public void setExploitationPlan(FileEntry exploitationPlan) {
        this.exploitationPlan = exploitationPlan;
    }

    public FileEntry getSignedGrantOfferLetter() {
        return signedGrantOfferLetter;
    }

    public void setSignedGrantOfferLetter(FileEntry signedGrantOfferLetter) {
        this.signedGrantOfferLetter = signedGrantOfferLetter;
    }

    public FileEntry getAdditionalContractFile() {
        return additionalContractFile;
    }

    public void setAdditionalContractFile(FileEntry additionalContractFile) {
        this.additionalContractFile = additionalContractFile;
    }

    public boolean isOfferRejected() {
        return offerRejected;
    }

    public void setOfferRejected(boolean offerRejected) {
        this.offerRejected = offerRejected;
    }

    public FileEntry getGrantOfferLetter() {
        return grantOfferLetter;
    }

    public void setGrantOfferLetter(FileEntry grantOfferLetter) {
        this.grantOfferLetter = grantOfferLetter;
    }

    public Boolean getOtherDocumentsApproved() {
        return otherDocumentsApproved;
    }

    public void setOtherDocumentsApproved(Boolean otherDocumentsApproved) {
        this.otherDocumentsApproved = otherDocumentsApproved;
    }
}
