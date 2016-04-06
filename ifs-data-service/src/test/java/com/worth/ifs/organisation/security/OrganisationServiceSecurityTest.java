package com.worth.ifs.organisation.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.organisation.transactional.OrganisationService;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.address.domain.AddressType.REGISTERED;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static java.util.Arrays.asList;
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

    private OrganisationPermissionRules organisationRules;

    @Before
    public void lookupPermissionRules() {

        organisationRules = getMockPermissionRulesBean(OrganisationPermissionRules.class);

        MockitoAnnotations.initMocks(this);
    }

    @Override
    protected Class<? extends OrganisationService> getServiceClass() {
        return TestOrganisationService.class;
    }

    @Test
    public void testFindByApplicationId() {

        ServiceResult<Set<OrganisationResource>> results = service.findByApplicationId(1L);
        assertEquals(0, results.getSuccessObject().size());

        verify(organisationRules, times(2)).anyoneCanSeeOrganisationsNotYetConnectedToApplications(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verify(organisationRules, times(2)).memberOfOrganisationCanViewOwnOrganisation(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verify(organisationRules, times(2)).usersCanViewOrganisationsOnTheirOwnApplications(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(organisationRules);
    }

    @Test(expected = AccessDeniedException.class)
    public void testFindById() {

        service.findById(1L);

        verify(organisationRules).anyoneCanSeeOrganisationsNotYetConnectedToApplications(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verify(organisationRules).memberOfOrganisationCanViewOwnOrganisation(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verify(organisationRules).usersCanViewOrganisationsOnTheirOwnApplications(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(organisationRules);
    }

    @Test(expected = AccessDeniedException.class)
    public void testCreate() {

        service.create(newOrganisationResource().build());

        verify(organisationRules).anyoneCanCreateOrganisations(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(organisationRules);
    }

    @Test(expected = AccessDeniedException.class)
    public void testUpdate() {

        service.update(newOrganisationResource().build());

        verify(organisationRules).anyoneCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(organisationRules);
    }

    @Test(expected = AccessDeniedException.class)
    public void testAddAddress() {

        service.addAddress(1L, REGISTERED, newAddressResource().build());

        verify(organisationRules).anyoneCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(isA(OrganisationResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(organisationRules);
    }


    public void testSearchAcademic() {

        ServiceResult<List<OrganisationSearchResult>> results = service.searchAcademic("Univer", 10);
        assertEquals(0, results.getSuccessObject().size());

        verify(organisationRules, times(2)).anyoneCanSeeOrganisationSearchResults(isA(OrganisationSearchResult.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(organisationRules);
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetSearchOrganisation() {

        service.getSearchOrganisation(1L);

        verify(organisationRules).anyoneCanSeeOrganisationSearchResults(isA(OrganisationSearchResult.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(organisationRules);
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    private static class TestOrganisationService implements OrganisationService {

        @Override
        public ServiceResult<Set<OrganisationResource>> findByApplicationId(Long applicationId) {
            return serviceSuccess(newOrganisationResource().buildSet(2));
        }

        @Override
        public ServiceResult<OrganisationResource> findById(Long organisationId) {
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
        public ServiceResult<OrganisationResource> addAddress(Long organisationId, AddressType addressType, AddressResource addressResource) {
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
