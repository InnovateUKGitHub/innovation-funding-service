package com.worth.ifs.testdata.builders;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.testdata.builders.data.ProjectData;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Generates data from Competitions, including any Applications taking part in this Competition
 */
public class ProjectDataBuilder extends BaseDataBuilder<ProjectData, ProjectDataBuilder> {

    public ProjectDataBuilder withExistingProject(String projectName) {
        return with(data -> {

            doAs(systemRegistrar(), () -> {
                Long applicationId = applicationRepository.findByName(projectName).get(0).getId();
                doAs(compAdmin(), () -> data.setApplication(applicationService.getApplicationById(applicationId).getSuccessObjectOrThrowException()));
                data.setLeadApplicant(userService.getUserById(retrieveLeadApplicant(applicationId).getUser()).getSuccessObjectOrThrowException());
            });

            doAs(data.getLeadApplicant(), () ->
                data.setProject(projectService.getByApplicationId(data.getApplication().getId()).getSuccessObjectOrThrowException())
            );
        });
    }

    public ProjectDataBuilder withStartDate(LocalDate startDate) {
        return with(data -> doAs(data.getLeadApplicant(), () ->
            projectService.updateProjectStartDate(data.getProject().getId(), startDate).getSuccessObjectOrThrowException()
        ));
    }

    public ProjectDataBuilder withProjectManager(String email) {
        return with(data -> doAs(data.getLeadApplicant(), () -> {
            User projectManager = userRepository.findByEmail(email).get();
            projectService.setProjectManager(data.getProject().getId(), projectManager.getId()).getSuccessObjectOrThrowException();
            data.setProjectManager(userService.getUserById(projectManager.getId()).getSuccessObjectOrThrowException());
        }));
    }

    public ProjectDataBuilder withProjectAddressOrganisationAddress() {
        return with(data -> doAs(data.getLeadApplicant(), () -> {
            Long leadOrganisationId = data.getLeadApplicant().getOrganisations().get(0);
            OrganisationResource leadOrganisation = organisationService.findById(leadOrganisationId).getSuccessObjectOrThrowException();
            AddressResource address = leadOrganisation.getAddresses().get(0).getAddress();
            projectService.updateProjectAddress(leadOrganisationId, data.getProject().getId(), OrganisationAddressType.PROJECT, address).getSuccessObjectOrThrowException();
        }));
    }

    public ProjectDataBuilder submitProjectDetails() {
        return with(data -> doAs(data.getProjectManager(), () -> {
            projectService.submitProjectDetails(data.getProject().getId(), LocalDateTime.now()).getSuccessObjectOrThrowException();
        }));
    }

    public ProjectDataBuilder withFinanceContact(String organisationName, String financeContactEmail) {
        return with(data -> {
            UserResource financeContact = retrieveUserByEmail(financeContactEmail);
            Organisation organisation = retrieveOrganisationByName(organisationName);

            UserResource partnerUser = findAnyPartnerForOrganisation(data, organisation.getId());

            doAs(partnerUser, () -> projectService.updateFinanceContact(data.getProject().getId(), organisation.getId(), financeContact.getId()).
                    getSuccessObjectOrThrowException());
        });
    }

    public ProjectDataBuilder withMonitoringOfficer(String firstName, String lastName, String email, String phoneNumber) {
        return with(data -> {
            List<User> projectFinanceUsers = userRepository.findByRoles_Name(UserRoleType.PROJECT_FINANCE.getName());
            UserResource projectFinanceUser = retrieveUserById(projectFinanceUsers.get(0).getId());

            doAs(projectFinanceUser, () -> {
                MonitoringOfficerResource mo = new MonitoringOfficerResource(firstName, lastName, email, phoneNumber, data.getProject().getId());
                projectService.saveMonitoringOfficer(data.getProject().getId(), mo).getSuccessObjectOrThrowException();
            });
        });
    }

    public ProjectDataBuilder withBankDetails(String organisationName, String accountNumber, String sortCode) {
        return with(data -> {

            Organisation organisation = retrieveOrganisationByName(organisationName);

            doAs(findAnyPartnerForOrganisation(data, organisation.getId()), () -> {

                OrganisationResource organisationResource = organisationService.findById(organisation.getId()).getSuccessObjectOrThrowException();

                BankDetailsResource bankDetails = new BankDetailsResource();
                bankDetails.setAccountNumber(accountNumber);
                bankDetails.setSortCode(sortCode);
                bankDetails.setProject(data.getProject().getId());
                bankDetails.setOrganisation(organisation.getId());
                bankDetails.setCompanyName(organisation.getName());
                bankDetails.setOrganisationAddress(organisationResource.getAddresses().get(0));
                bankDetails.setOrganisationTypeName(organisation.getOrganisationType().getName());
                bankDetails.setRegistrationNumber(organisation.getCompanyHouseNumber());

                bankDetailsService.submitBankDetails(bankDetails).getSuccessObjectOrThrowException();
            });
        });
    }

    private UserResource findAnyPartnerForOrganisation(ProjectData data, Long organisationId) {

        List<ProjectUser> organisationPartners = projectUserRepository.findByProjectIdAndOrganisationId(data.getProject().getId(), organisationId);
        return retrieveUserById(organisationPartners.get(0).getUser().getId());
    }

    public static ProjectDataBuilder newProjectData(ServiceLocator serviceLocator) {
        return new ProjectDataBuilder(emptyList(), serviceLocator);
    }

    private ProjectDataBuilder(List<BiConsumer<Integer, ProjectData>> multiActions,
                               ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected ProjectDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectData>> actions) {
        return new ProjectDataBuilder(actions, serviceLocator);
    }

    @Override
    protected ProjectData createInitial() {
        return new ProjectData();
    }


}
