package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.LambdaMatcher;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.service.SilCrmEndpoint;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests around the {@link CrmServiceImpl}.
 */
public class CrmServiceImplTest extends BaseServiceUnitTest<CrmServiceImpl> {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private SilCrmEndpoint silCrmEndpoint;

    @Override
    protected CrmServiceImpl supplyServiceUnderTest() {
        return new CrmServiceImpl();
    }

    @Test
    public void testSycCrmContact() {
        Long userId = 1L;
        User user = newUser().build();
        Organisation organisation = newOrganisation().build();
        when(userRepository.findOne(userId)).thenReturn(user);
        when(organisationRepository.findByUsersId(userId)).thenReturn(asList(organisation));
        when(silCrmEndpoint.updateContact(any(SilContact.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.syncCrmContact(userId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(silCrmEndpoint).updateContact(LambdaMatcher.createLambdaMatcher(matchSilContact(user, organisation)));

    }

    private Predicate<SilContact> matchSilContact(User user, Organisation organisation) {
        return silContact -> {
            assertThat(silContact.getSrcSysContactId(), equalTo(String.valueOf(user.getId())));
            assertThat(silContact.getOrganisation().getSrcSysOrgId(), equalTo(String.valueOf(organisation.getId())));
            return true;
        };
    }
}