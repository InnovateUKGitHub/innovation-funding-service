package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;
import org.innovateuk.ifs.invite.mapper.ApplicationKtaInviteMapper;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteBuilder.newApplicationKtaInvite;
import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Test for the lookup strategies employed by the permission system to look up entities based on keys
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationKtaInviteLookupStrategyTest {

    @InjectMocks
    private ApplicationKtaInviteLookupStrategy lookup;

    @Mock
    private ApplicationKtaInviteMapper applicationKtaInviteMapper;

    @Mock
    private ApplicationKtaInviteRepository applicationKtaInviteRepository;

    private final Long inviteId = 123L;

    @Test
    public void testFindById() {
        ApplicationKtaInviteResource applicationKtaInviteResource = newApplicationKtaInviteResource().withId(inviteId).build();
        ApplicationKtaInvite applicationKtaInvite = newApplicationKtaInvite().withId(inviteId).build();
        when(applicationKtaInviteMapper.mapToResource(applicationKtaInvite)).thenReturn(applicationKtaInviteResource);
        when(applicationKtaInviteRepository.findById(inviteId)).thenReturn(Optional.of(applicationKtaInvite));
        assertEquals(applicationKtaInviteResource, lookup.getApplicationInviteResource(inviteId));
    }
}
