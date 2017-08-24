package org.innovateuk.ifs.organisation.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeEnum;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.resource.AddressTypeEnum.REGISTERED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Testing the permission rules applied to the secured methods in OrganisationService.  This set of tests tests for the
 * individual rules that are called whenever an OrganisationService method is called.  They do not however test the logic
 * within those rules
 */
public class OrganisationServiceSecurityTest extends BaseServiceSecurityTest<OrganisationService> {

    private OrganisationPermissionRules rules;
    private OrganisationLookupStrategies lookup;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(OrganisationPermissionRules.class);
        lookup = getMockPermissionEntityLookupStrategiesBean(OrganisationLookupStrategies.class);
    }

    @Override
    protected Class<? extends OrganisationService> getClassUnderTest() {
        return TestOrganisationService.class;
    }

    @Test
    public void testFindByApplicationId() {

        ServiceResult<Set<OrganisationResource>> results = classUnderTest.findByApplicationId(1L);
        assertEquals(0, results.getSuccessObject().size());

        verify(rules, times(2)).systemRegistrationUserCanSeeOrganisationsNotYetConnectedToApplications(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verify(rules, times(2)).memberOfOrganisationCanViewOwnOrganisation(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verify(rules, times(2)).usersCanViewOrganisationsOnTheirOwnApplications(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verify(rules, times(2)).internalUsersCanSeeAllOrganisations(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verify(rules, times(2)).systemRegistrationUserCanSeeAllOrganisations(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verify(rules, times(2)).projectPartnerUserCanSeePartnerOrganisationsWithinTheirProjects(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void testFindById() {
        assertAccessDenied(() -> classUnderTest.findById(1L), () -> {
            verify(rules).systemRegistrationUserCanSeeOrganisationsNotYetConnectedToApplications(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).memberOfOrganisationCanViewOwnOrganisation(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).usersCanViewOrganisationsOnTheirOwnApplications(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).internalUsersCanSeeAllOrganisations(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).systemRegistrationUserCanSeeAllOrganisations(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).projectPartnerUserCanSeePartnerOrganisationsWithinTheirProjects(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void testGetPrimaryForUser() {
        assertAccessDenied(() -> classUnderTest.findById(1L), () -> {
            verify(rules).systemRegistrationUserCanSeeOrganisationsNotYetConnectedToApplications(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).memberOfOrganisationCanViewOwnOrganisation(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).usersCanViewOrganisationsOnTheirOwnApplications(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).internalUsersCanSeeAllOrganisations(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).systemRegistrationUserCanSeeAllOrganisations(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).projectPartnerUserCanSeePartnerOrganisationsWithinTheirProjects(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void testCreate() {
        assertAccessDenied(() -> classUnderTest.create(newOrganisationResource().build()), () -> {
            verify(rules).systemRegistrationUserCanCreateOrganisations(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void testUpdate() {
        assertAccessDenied(() -> classUnderTest.update(newOrganisationResource().build()), () -> {
            verify(rules).systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).memberOfOrganisationCanUpdateOwnOrganisation(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).projectFinanceUserCanUpdateAnyOrganisation(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void testAddAddress() {

        when(lookup.findOrganisationById(123L)).thenReturn(newOrganisationResource().build());

        assertAccessDenied(() -> classUnderTest.addAddress(123L, REGISTERED, newAddressResource().build()), () -> {
            verify(rules).systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).memberOfOrganisationCanUpdateOwnOrganisation(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verify(rules).projectFinanceUserCanUpdateAnyOrganisation(isA(OrganisationResource.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void testSearchAcademic() {

        ServiceResult<List<OrganisationSearchResult>> results = classUnderTest.searchAcademic("Univer", 10);
        assertEquals(0, results.getSuccessObject().size());

        verify(rules, times(2)).systemRegistrationUserCanSeeOrganisationSearchResults(isA(OrganisationSearchResult.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void testGetSearchOrganisation() {
        assertAccessDenied(() -> classUnderTest.getSearchOrganisation(1L), () -> {
            verify(rules).systemRegistrationUserCanSeeOrganisationSearchResults(isA(OrganisationSearchResult.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(rules);
        });
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestOrganisationService implements OrganisationService {

        @Override
        public ServiceResult<Set<OrganisationResource>> findByApplicationId(Long applicationId) {
            return serviceSuccess(newOrganisationResource().buildSet(2));
        }

        @Override
        public ServiceResult<OrganisationResource> findById(Long organisationId) {
            return serviceSuccess(newOrganisationResource().build());
        }

        @Override
        public ServiceResult<OrganisationResource> getPrimaryForUser(Long userId) {
            return serviceSuccess(newOrganisationResource().build());
        }

        @Override
        public ServiceResult<OrganisationResource> create(OrganisationResource organisation) {
            return null;
        }

        @Override
        public ServiceResult<OrganisationResource> update(OrganisationResource organisationResource) {
            return null;
        }

        @Override
        public ServiceResult<OrganisationResource> updateOrganisationNameAndRegistration(Long organisationId, String organisationName, String registrationNumber) {
            return null;
        }

        @Override
        public ServiceResult<OrganisationResource> addAddress(Long organisationId, AddressTypeEnum addressType, AddressResource addressResource) {
            return null;
        }

        @Override
        public ServiceResult<List<OrganisationSearchResult>> searchAcademic(String organisationName, int maxItems) {
            return serviceSuccess(new ArrayList<>(asList(new OrganisationSearchResult(), new OrganisationSearchResult())));
        }

        @Override
        public ServiceResult<OrganisationSearchResult> getSearchOrganisation(Long searchOrganisationId) {
            return serviceSuccess(new OrganisationSearchResult());
        }
    }
}
