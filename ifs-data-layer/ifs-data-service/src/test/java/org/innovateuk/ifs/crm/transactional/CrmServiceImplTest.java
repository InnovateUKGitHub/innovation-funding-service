package org.innovateuk.ifs.crm.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.LambdaMatcher;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.service.SilCrmEndpoint;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
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
        return new CrmServiceImpl();
    }

    @Test
    public void testSycCrmContact() {
        Long userId = 1L;
        UserResource user = newUserResource().build();
        OrganisationResource organisation = newOrganisationResource().withCompanyHouseNumber("Something").build();
        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(user));
        when(organisationService.getPrimaryForUser(userId)).thenReturn(serviceSuccess(organisation));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.syncCrmContact(userId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchSilContact(user, organisation)));

    }

    private Predicate<SilContact> matchSilContact(UserResource user, OrganisationResource organisation) {
        return silContact -> {
            assertThat(silContact.getSrcSysContactId(), equalTo(String.valueOf(user.getId())));
            assertThat(silContact.getOrganisation().getRegistrationNumber(), equalTo(organisation.getCompanyHouseNumber()));
            return true;
        };
    }
}