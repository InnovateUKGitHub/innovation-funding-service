package org.innovateuk.ifs.project.core.domain;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.financereviewer.domain.FinanceReviewer;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.user.domain.ProcessActivity;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static javax.persistence.EnumType.STRING;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 *  A project represents an application that has been accepted (and is now in project setup phase).
 *  It stores details specific to project (which are different from application)
 */
@Entity
public class Project implements ProcessActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="applicationId", referencedColumnName = "id")
    private Application application;

    private LocalDate targetStartDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="address", referencedColumnName="id")
    private Address address;

    @Min(0)
    private Long durationInMonths;

    private String name;

    private ZonedDateTime documentsSubmittedDate;

    private ZonedDateTime offerSubmittedDate;

    private String grantOfferLetterRejectionReason;

    private ZonedDateTime spendProfileSubmittedDate;

    @OneToMany(mappedBy="project", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = ProjectUser.class)
    private List<ProjectUser> projectUsers = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, targetEntity = MonitoringOfficer.class, mappedBy = "project", fetch = FetchType.LAZY)
    private MonitoringOfficer projectMonitoringOfficer = null;

    @OneToOne(cascade = CascadeType.ALL, targetEntity = FinanceReviewer.class,
            mappedBy = "project", fetch = FetchType.LAZY, orphanRemoval = true)
    private FinanceReviewer financeReviewer = null;

    @OneToMany(mappedBy="project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnerOrganisation> partnerOrganisations = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="signedGrantOfferFileEntryId", referencedColumnName = "id")
    private FileEntry signedGrantOfferLetter;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="grantOfferLetterFileEntryId", referencedColumnName = "id")
    private FileEntry grantOfferLetter;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="additionalContractFileEntryId", referencedColumnName = "id")
    private FileEntry additionalContractFile;

    @NotNull
    @Enumerated(STRING)
    private ApprovalType otherDocumentsApproved = ApprovalType.UNSET;

    @OneToMany(mappedBy="project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpendProfile> spendProfiles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", cascade = {CascadeType.REMOVE})
    private List<ProjectDocument> projectDocuments = new ArrayList<>();

    @OneToOne(mappedBy = "target", cascade = CascadeType.ALL, optional=true, fetch = FetchType.LAZY)
    private ProjectProcess projectProcess;

    public Project() {}

    public Project(Application application, LocalDate targetStartDate, Address address,
                   Long durationInMonths, String name, ZonedDateTime documentsSubmittedDate, ApprovalType otherDocumentsApproved ) {

        this.application = application;
        this.targetStartDate = targetStartDate;
        this.address = address;
        this.durationInMonths = durationInMonths;
        this.name = name;
        this.otherDocumentsApproved = otherDocumentsApproved;
        this.documentsSubmittedDate = documentsSubmittedDate;
    }

    public void addProjectUser(ProjectUser projectUser) {
        projectUsers.add(projectUser);
    }

    public void setProjectMonitoringOfficer(MonitoringOfficer projectMonitoringOfficer) {
        this.projectMonitoringOfficer = projectMonitoringOfficer;
    }

    public FinanceReviewer getFinanceReviewer() {
        return financeReviewer;
    }

    public void setFinanceReviewer(FinanceReviewer financeReviewer) {
        this.financeReviewer = financeReviewer;
    }

    public void addPartnerOrganisation(PartnerOrganisation partnerOrganisation) {
        partnerOrganisations.add(partnerOrganisation);
    }

    public boolean removeProjectUser(ProjectUser projectUser) {
        return projectUsers.remove(projectUser);
    }

    public boolean removeProjectUsers(List<ProjectUser> projectUserstoRemove) {
        return projectUsers.removeAll(projectUserstoRemove);
    }

    public ProjectUser getExistingProjectUserWithRoleForOrganisation(ProjectParticipantRole role, Organisation organisation) {
        List<ProjectUser> matchingUser = simpleFilter(projectUsers, projectUser -> projectUser.getRole()==role && projectUser.getOrganisation().equals(organisation));

        if (matchingUser.isEmpty()) {
            return null;
        }

        return getOnlyElement(matchingUser);
    }

    public Optional<PartnerOrganisation> getLeadOrganisation() {
        return getPartnerOrganisations().stream()
                .filter(PartnerOrganisation::isLeadOrganisation)
                .findFirst();
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
        return simpleFilter(projectUsers, filter);
    }

    public List<ProjectUser> getProjectUsersWithRole(ProjectParticipantRole... roles){
        return getProjectUsers(pu -> Arrays.stream(roles).anyMatch(pu.getRole()::equals));
    }

    public List<Organisation> getOrganisations(){
        return simpleMap(partnerOrganisations, PartnerOrganisation::getOrganisation);
    }

    public List<Organisation> getOrganisations(Predicate<Organisation> predicate){
        return simpleFilter(getOrganisations(), predicate);
    }

    public List<PartnerOrganisation> getPartnerOrganisations() {
        return partnerOrganisations;
    }

    public void setProjectUsers(List<ProjectUser> projectUsers) {
        this.projectUsers.clear();
        this.projectUsers.addAll(projectUsers);
    }

    public void removeProjectMonitoringOfficer() {
        this.projectMonitoringOfficer = null;
    }

    public void setPartnerOrganisations(List<PartnerOrganisation> partnerOrganisations) {
        this.partnerOrganisations.clear();
        this.partnerOrganisations.addAll(partnerOrganisations);
    }

    public ZonedDateTime getDocumentsSubmittedDate() {
        return documentsSubmittedDate;
    }

    public void setDocumentsSubmittedDate(ZonedDateTime documentsSubmittedDate) {
        this.documentsSubmittedDate = documentsSubmittedDate;
    }

    public ZonedDateTime getOfferSubmittedDate() {
        return offerSubmittedDate;
    }

    public void setOfferSubmittedDate(ZonedDateTime offerSubmittedDate) {
        this.offerSubmittedDate = offerSubmittedDate;
    }

    public String getGrantOfferLetterRejectionReason() {
        return grantOfferLetterRejectionReason;
    }

    public void setGrantOfferLetterRejectionReason(String grantOfferLetterRejectionReason) {
        this.grantOfferLetterRejectionReason = grantOfferLetterRejectionReason;
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

    public FileEntry getGrantOfferLetter() {
        return grantOfferLetter;
    }

    public void setGrantOfferLetter(FileEntry grantOfferLetter) {
        this.grantOfferLetter = grantOfferLetter;
    }

    @NotNull
    public ApprovalType getOtherDocumentsApproved() {
        return otherDocumentsApproved;
    }

    public void setOtherDocumentsApproved(@NotNull ApprovalType otherDocumentsApproved) {
        this.otherDocumentsApproved = otherDocumentsApproved;
    }

    public ZonedDateTime getSpendProfileSubmittedDate() {
        return spendProfileSubmittedDate;
    }

    public void setSpendProfileSubmittedDate(ZonedDateTime spendProfileSubmittedDate) {
        this.spendProfileSubmittedDate = spendProfileSubmittedDate;
    }

    public List<SpendProfile> getSpendProfiles() {
        return spendProfiles;
    }

    public void setSpendProfiles(List<SpendProfile> spendProfiles) {
        this.spendProfiles = spendProfiles;
    }

    public List<ProjectDocument> getProjectDocuments() {
        return projectDocuments;
    }

    public void setProjectDocuments(List<ProjectDocument> projectDocuments) {
        this.projectDocuments = projectDocuments;
    }

    public boolean isPartner(User user) {
        return !getProjectUsers(projectUser -> projectUserForUser(user, projectUser)).isEmpty();
    }

    public boolean isProjectManager(User user) {
        return !getProjectUsers(projectUser ->
                projectUserForUser(user, projectUser) &&
                projectUser.isProjectManager()).isEmpty();
    }

    private boolean projectUserForUser(User user, ProjectUser projectUser) {
        return projectUser.getUser().getId().equals(user.getId());
    }

    public Optional<MonitoringOfficer> getProjectMonitoringOfficer() {
        return Optional.ofNullable(projectMonitoringOfficer);
    }

    public MonitoringOfficer getProjectMonitoringOfficerOrElseNull() {
        return getProjectMonitoringOfficer().orElse(null);
    }

    public ProjectProcess getProjectProcess() {
        return projectProcess;
    }

    public boolean isSpendProfileGenerated() { return !spendProfiles.isEmpty(); }

    public boolean isCollaborativeProject() {
        return partnerOrganisations.size() != 1;
    }
}