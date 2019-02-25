package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.Viability;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.testdata.builders.data.ProjectData;
import org.innovateuk.ifs.testdata.services.CsvUtils;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;

/**
 * Generates data from Competitions, including any Applications taking part in this Competition
 */
public class ProjectDataBuilder extends BaseDataBuilder<ProjectData, ProjectDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectData.class);

    public ProjectDataBuilder withExistingProject(String projectName) {
        return with(data -> {

            doAs(systemRegistrar(), () -> {
                Long applicationId = applicationRepository.findByName(projectName).get(0).getId();
                doAs(compAdmin(), () -> data.setApplication(applicationService.getApplicationById(applicationId).getSuccess()));
                data.setLeadApplicant(baseUserService.getUserById(retrieveLeadApplicant(applicationId).getUser()).getSuccess());
            });

            doAs(data.getLeadApplicant(), () ->
                data.setProject(projectService.getByApplicationId(data.getApplication().getId()).getSuccess())
            );
        });
    }

    public ProjectDataBuilder withStartDate(LocalDate startDate) {
        return with(data -> doAs(data.getLeadApplicant(), () ->
                projectDetailsService.updateProjectStartDate(data.getProject().getId(), startDate).getSuccess()
        ));
    }

    public ProjectDataBuilder withProjectManager(String email) {
        return with(data -> doAs(data.getLeadApplicant(), () -> {
            User projectManager = userRepository.findByEmail(email).get();
            projectDetailsService.setProjectManager(data.getProject().getId(), projectManager.getId()).getSuccess();
            data.setProjectManager(baseUserService.getUserById(projectManager.getId()).getSuccess());
        }));
    }

    public ProjectDataBuilder withProjectAddressOrganisationAddress(List<CsvUtils.OrganisationLine> organisationLines) {
        return with(data -> doAs(data.getLeadApplicant(), () -> {
            Long leadApplicantId = data.getLeadApplicant().getId();
            OrganisationResource leadOrganisation = organisationService.getByUserAndApplicationId(leadApplicantId, data.getApplication().getId()).getSuccess();
            projectDetailsService.updateProjectAddress(leadOrganisation.getId(), data.getProject().getId(), getAddress(leadOrganisation, organisationLines)).getSuccess();
        }));
    }

    public ProjectDataBuilder withFinanceContact(String organisationName, String financeContactEmail) {
        return with(data -> {
            UserResource financeContact = retrieveUserByEmail(financeContactEmail);
            Organisation organisation = retrieveOrganisationByName(organisationName);

            UserResource partnerUser = findAnyPartnerForOrganisation(data, organisation.getId());

            doAs(partnerUser, () -> projectDetailsService.updateFinanceContact(new ProjectOrganisationCompositeId(data.getProject().getId(), organisation.getId()), financeContact.getId()).
                    getSuccess());
        });
    }

    public ProjectDataBuilder withApprovedFinanceChecks(Boolean generateSpendProfile) {
        return with(data -> doAs(anyProjectFinanceUser(), () -> {
            Set<OrganisationResource> organisations = organisationService.findByApplicationId(data.getApplication().getId()).getSuccess();
            for (OrganisationResource org : organisations) {
                updateFinanceChecks(data.getProject().getId(), org.getId());
            }
            if (generateSpendProfile) {
                spendProfileService.generateSpendProfile(data.getProject().getId()).getSuccess();
            }
        }));
    }

    public ProjectDataBuilder withSpendProfile(Boolean approveSpendProfile) {
        return with(data -> {
            uploadSpendProfile(data);
            submitSpendProfile(data);
            if (approveSpendProfile) {
                approveSpendProfile(data);
            }
        });
    }

    private void uploadSpendProfile(ProjectData data) {
        doAs(anyProjectFinanceUser(), () -> {
            Set<OrganisationResource> organisations = organisationService.findByApplicationId(data.getApplication().getId()).getSuccess();
            for (OrganisationResource org : organisations) {
                doAs(findAnyPartnerForOrganisation(data, org.getId()), () -> {
                    spendProfileService.markSpendProfileComplete(new ProjectOrganisationCompositeId(data.getProject().getId(), org.getId()));
                });
            }
        });
    }

    private void submitSpendProfile(ProjectData data) {
        doAs(data.getProjectManager(), () -> {
            spendProfileService.completeSpendProfilesReview(data.getProject().getId());
        });
    }

    private void approveSpendProfile(ProjectData data) {
        doAs(anyProjectFinanceUser(), () -> {
            spendProfileService.approveOrRejectSpendProfile(data.getProject().getId(), ApprovalType.APPROVED);
        });
    }

    private void updateFinanceChecks(Long projectId, Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        financeCheckService.saveViability(projectOrganisationCompositeId, Viability.APPROVED, ViabilityRagStatus.GREEN).getSuccess();
        financeCheckService.saveEligibility(projectOrganisationCompositeId, EligibilityState.APPROVED, EligibilityRagStatus.GREEN).getSuccess();
    }

    public ProjectDataBuilder withMonitoringOfficer(String firstName, String lastName, String email, String phoneNumber) {
        return with(data -> doAs(anyProjectFinanceUser(), () -> {
            MonitoringOfficerResource mo = new MonitoringOfficerResource(firstName, lastName, email, phoneNumber, data.getProject().getId());
            monitoringOfficerService.saveMonitoringOfficer(data.getProject().getId(), mo).getSuccess();
        }));
    }

    public ProjectDataBuilder withProjectDocuments() {
        return with(data -> doAs(data.getProjectManager(), () -> {
            List<CompetitionDocument> competitionDocuments = competitionDocumentConfigRepository.findByCompetitionId(data.getApplication().getCompetition());
            List<PartnerOrganisation> projectOrganisations = partnerOrganisationRepository.findByProjectId(data.getProject().getId());

            if (projectOrganisations.size() == 1) {
                competitionDocuments.removeIf(
                        document -> document.getTitle().equals(COLLABORATION_AGREEMENT_TITLE));
            }

            competitionDocuments.stream()
                    .forEach(competitionDocument -> uploadProjectDocument(data, competitionDocument.getId()));
            submitProjectDocuments(data, competitionDocuments);
            approveProjectDocument(data, competitionDocuments);
        }));
    }

    public ProjectDataBuilder withPublishGrantOfferLetter() {
        return with(data -> doAs(anyProjectFinanceUser(), () -> {
            grantOfferLetterService.sendGrantOfferLetter(data.getProject().getId());
        }));
    }

    public ProjectDataBuilder withSignedGrantOfferLetter() {
        return with(data -> doAs(data.getProjectManager(), () -> {
            /*
            *
            *
            * Here do the logic to upload a grant offer letter
            * something like service.upload signed offer letter
            *
            *
            * */
//            grantOfferLetterService.submitGrantOfferLetter(data.getProject().getId())
        }));
    }

    private void uploadProjectDocument(ProjectData data, long documentConfigId)  {
        try {
            File file = new File(ProjectDataBuilder.class.getResource("/webtest.pdf").toURI());
            InputStream inputStream = new FileInputStream(file);
            Supplier<InputStream> inputStreamSupplier = () -> inputStream;
            documentsService.createDocumentFileEntry(data.getProject().getId(), documentConfigId, new FileEntryResource(null, "testing.pdf", "application/pdf", file.length()), inputStreamSupplier);
        } catch (Exception e) {
            LOG.error("Unable to create project document file", e);
            throw new RuntimeException(e);
        }
    }

    private void submitProjectDocuments(ProjectData data, List<CompetitionDocument> competitionDocuments) {
        Project project = projectRepository.findById(data.getProject().getId()).get();
        project.setDocumentsSubmittedDate(ZonedDateTime.now());
        project.setProjectDocuments(projectDocumentRepository.findAllByProjectId(data.getProject().getId()));
        competitionDocuments.stream()
                .forEach(competitionDocument -> documentsService.submitDocument(data.getProject().getId(), competitionDocument.getId()));
    }

    public void approveProjectDocument(ProjectData data, List<CompetitionDocument> competitionDocuments) {
         doAs(anyProjectFinanceUser(), () -> {
            ProjectDocumentDecision projectDocumentDecision = new ProjectDocumentDecision(true, null);
            competitionDocuments.stream()
                    .forEach(competitionDocument -> documentsService.documentDecision(data.getProject().getId(), competitionDocument.getId(), projectDocumentDecision));
        });
    }

    public ProjectDataBuilder withBankDetails(String organisationName, String accountNumber, String sortCode, List<CsvUtils.OrganisationLine> organisationLines, boolean bankDetailsApproved) {
        return with(data -> {

            Organisation organisation = retrieveOrganisationByName(organisationName);

            doAs(findAnyPartnerForOrganisation(data, organisation.getId()), () -> {

                OrganisationResource organisationResource = organisationService.findById(organisation.getId()).getSuccess();
                OrganisationTypeResource organisationType = organisationTypeService.findOne(organisationResource.getOrganisationType()).getSuccess();
                BankDetailsResource bankDetails = new BankDetailsResource();
                bankDetails.setAccountNumber(accountNumber);
                bankDetails.setSortCode(sortCode);
                bankDetails.setProject(data.getProject().getId());
                bankDetails.setOrganisation(organisationResource.getId());
                bankDetails.setCompanyName(organisationResource.getName());
                bankDetails.setAddress(getAddress(organisationResource, organisationLines));
                bankDetails.setOrganisationTypeName(organisationType.getName());
                bankDetails.setRegistrationNumber(organisationResource.getCompaniesHouseNumber());
                bankDetails.setManualApproval(bankDetailsApproved);

                bankDetailsService.submitBankDetails(bankDetails).getSuccess();
            });
        });
    }

    public ProjectDataBuilder withAmendedStatus(ProjectState state) {

        return asIfsAdmin(data -> {
            if (ProjectState.WITHDRAWN.equals(state)) {
                projectService.withdrawProject(data.getProject().getId()).getSuccess();
                applicationService.withdrawApplication(data.getApplication().getId()).getSuccess();
            }
        });
    }

    private UserResource anyProjectFinanceUser() {
        List<User> projectFinanceUsers = userRepository.findByRoles(Role.PROJECT_FINANCE);
        return retrieveUserById(projectFinanceUsers.get(0).getId());
    }

    private UserResource findAnyPartnerForOrganisation(ProjectData data, Long organisationId) {
        return testService.doWithinTransaction(() -> {
            List<ProjectUser> organisationPartners = projectUserRepository.findByProjectIdAndOrganisationId(data.getProject().getId(), organisationId);
            return retrieveUserById(organisationPartners.get(0).getUser().getId());
        });
    }

    public static ProjectDataBuilder newProjectData(ServiceLocator serviceLocator) {
        return new ProjectDataBuilder(emptyList(), serviceLocator);
    }

    private ProjectDataBuilder(List<BiConsumer<Integer, ProjectData>> multiActions,
                               ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    private AddressResource getAddress(OrganisationResource organisationResource, List<CsvUtils.OrganisationLine> organisationLines) {
        CsvUtils.OrganisationLine organisationLine = organisationLines.stream().filter(line -> line.name.equals(organisationResource.getName())).findFirst().get();
        return newAddressResource().
                withId().
                withAddressLine1(organisationLine.addressLine1).
                withAddressLine2(organisationLine.addressLine2).
                withAddressLine3(organisationLine.addressLine3).
                withTown(organisationLine.town).
                withPostcode(organisationLine.postcode).
                withCounty(organisationLine.county).
                build();
    }

    @Override
    protected ProjectDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectData>> actions) {
        return new ProjectDataBuilder(actions, serviceLocator);
    }

    @Override
    protected ProjectData createInitial() {
        return new ProjectData();
    }

    @Override
    protected void postProcess(int index, ProjectData instance) {
        super.postProcess(index, instance);
        LOG.info("Created Project '{}'", instance.getProject().getName());
    }
}
