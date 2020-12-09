package org.innovateuk.ifs.crm.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.LambdaMatcher;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.service.SilCrmEndpoint;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationSicCodeResourceBuilder.newOrganisationSicCodeResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationExecutiveOfficerResourceBuilder.newOrganisationExecutiveOfficerResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests around the {@link CrmServiceImpl}.
 */
public class CrmServiceImplTest extends BaseServiceUnitTest<CrmServiceImpl> {

    @Mock
    private BaseUserService baseUserService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private SilCrmEndpoint silCrmEndpoint;

    @Override
    protected CrmServiceImpl supplyServiceUnderTest() {
        CrmServiceImpl service = new CrmServiceImpl();
        ReflectionTestUtils.setField(service, "newOrganisationSearchEnabled", true);
        return service;
    }

    @Test
    public void syncExternalCrmContact() {
        long userId = 1L;
        UserResource user = newUserResource().withRoleGlobal(APPLICANT).build();

        List<OrganisationResource> organisations = newOrganisationResource().withCompaniesHouseNumber("Something", "Else").build(2);

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getAllByUserId(userId)).thenReturn(serviceSuccess(organisations));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.syncCrmContact(userId);

        assertThat(result.isSuccess(), equalTo(true));

        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContact(user, organisations.get(0))));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContact(user, organisations.get(1))));
    }

    @Test
    public void syncExternalCrmContactWithOrganisationUpdates() {
        long userId = 1L;

        UserResource user = newUserResource()
                .withRoleGlobal(APPLICANT)
                .build();

        OrganisationResource organisation = newOrganisationResource()
                .withDateOfIncorporation(LocalDate.now())
                .withSicCodes(newOrganisationSicCodeResource().withSicCode("code-1", "code-2").build(2))
                .withExecutiveOfficers(newOrganisationExecutiveOfficerResource().withName("director-1", "director-2").build(2))
                .build();

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getAllByUserId(userId)).thenReturn(serviceSuccess(Collections.singletonList(organisation)));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.syncCrmContact(userId);

        assertThat(result.isSuccess(), equalTo(true));

        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContactWithOrganisationUpdates(user, organisation)));
    }

    @Test
    public void syncMonitoringOfficerOnlyCrmContact() {
        long userId = 1L;
        UserResource user = newUserResource().withRoleGlobal(MONITORING_OFFICER).build();

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getAllByUserId(userId)).thenReturn(serviceSuccess(Collections.emptyList()));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.syncCrmContact(userId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchMonitoringOfficerSilContact(user)));
    }

    @Test
    public void syncMonitoringOfficerAndExternalCrmContact() {
        long userId = 1L;
        UserResource user = newUserResource().withRolesGlobal(asList(APPLICANT,MONITORING_OFFICER)).build();
        List<OrganisationResource> organisations = newOrganisationResource().withCompaniesHouseNumber("Something", "Else").build(2);

        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getAllByUserId(userId)).thenReturn(serviceSuccess(organisations));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.syncCrmContact(userId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContact(user, organisations.get(0))));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchExternalSilContact(user, organisations.get(1))));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchMonitoringOfficerSilContact(user)));
    }


    private Predicate<SilContact> matchExternalSilContact(UserResource user, OrganisationResource organisation) {
        return silContact -> {
            assertThat(silContact.getSrcSysContactId(), equalTo(String.valueOf(user.getId())));
            assertThat(silContact.getOrganisation().getRegistrationNumber(), equalTo(organisation.getCompaniesHouseNumber()));
            return true;
        };
    }

    private Predicate<SilContact> matchExternalSilContactWithOrganisationUpdates(UserResource user, OrganisationResource organisation) {
        return silContact -> {
            assertThat(silContact.getOrganisation().getDateOfIncorporation(), equalTo(organisation.getDateOfIncorporation()));
            assertThat(silContact.getOrganisation().getSicCodes(), equalTo(organisation.getSicCodes().stream()
                    .map(OrganisationSicCodeResource::getSicCode)
                    .collect(Collectors.toList())));
            assertThat(silContact.getOrganisation().getExecutiveOfficers(), equalTo(organisation.getExecutiveOfficers().stream()
                    .map(OrganisationExecutiveOfficerResource::getName)
                    .collect(Collectors.toList())));
            return true;
        };
    }

    private Predicate<SilContact> matchMonitoringOfficerSilContact(UserResource user) {
        return silContact -> {
            assertThat(silContact.getSrcSysContactId(), equalTo(String.valueOf(user.getId())));
            assertThat(silContact.getOrganisation().getRegistrationNumber(), equalTo(""));
            return true;
        };
    }
}