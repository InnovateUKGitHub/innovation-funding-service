package com.worth.ifs.organisation.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.security.FormInputResponseFileUploadLookupStrategies;
import com.worth.ifs.application.security.FormInputResponseFileUploadRules;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.organisation.transactional.OrganisationService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.util.CollectionFunctions.asLinkedSet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing the permissions around the secured methods in OrganisationService
 */
public class OrganisationServiceSecurityTest extends BaseServiceSecurityTest<OrganisationService> {

    private OrganisationRules organisationRules;

    @Mock
    private static Supplier<Set<OrganisationResource>> organisationResourceSupplier;

    @Before
    public void lookupPermissionRules() {

        organisationRules = getMockPermissionRulesBean(OrganisationRules.class);

        MockitoAnnotations.initMocks(this);
    }

    @Override
    protected Class<? extends OrganisationService> getServiceClass() {
        return TestOrganisationService.class;
    }

    @Test
    public void testFindByApplicationId() {

        OrganisationResource organisation = newOrganisationResource().build();

        when(organisationResourceSupplier.get()).thenReturn(asLinkedSet(organisation));

        when(organisationRules.memberOfOrganisationCanViewOwnOrganisation(organisation, getLoggedInUser())).thenReturn(false);
        when(organisationRules.usersCanViewOrganisationsOnTheirOwnApplications(organisation, getLoggedInUser())).thenReturn(false);

        service.findByApplicationId(1L);

        verify(organisationRules).memberOfOrganisationCanViewOwnOrganisation(organisation, getLoggedInUser());
        verify(organisationRules).usersCanViewOrganisationsOnTheirOwnApplications(organisation, getLoggedInUser());
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    private static class TestOrganisationService implements OrganisationService {

        @Override
        public ServiceResult<Set<OrganisationResource>> findByApplicationId(Long applicationId) {
            return serviceSuccess(organisationResourceSupplier.get());
        }

        @Override
        public ServiceResult<OrganisationResource> findById(Long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<OrganisationResource> create(Organisation organisation) {
            return null;
        }

        @Override
        public ServiceResult<OrganisationResource> saveResource(OrganisationResource organisationResource) {
            return null;
        }

        @Override
        public ServiceResult<OrganisationResource> addAddress(Long organisationId, AddressType addressType, AddressResource addressResource) {
            return null;
        }

        @Override
        public ServiceResult<List<OrganisationSearchResult>> searchAcademic(String organisationName, int maxItems) {
            return null;
        }

        @Override
        public ServiceResult<OrganisationSearchResult> getSearchOrganisation(Long searchOrganisationId) {
            return null;
        }
    }
}
