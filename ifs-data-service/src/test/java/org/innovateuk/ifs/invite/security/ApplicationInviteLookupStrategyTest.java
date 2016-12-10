package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.mapper.ApplicationInviteMapper;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Test for the lookup strategies employed by the permission system to look up entities based on keys
 */
public class ApplicationInviteLookupStrategyTest {

    @InjectMocks
    private ApplicationInviteLookupStrategy lookup;

    @Mock
    private ApplicationInviteMapper applicationInviteMapper;

    @Mock
    private ApplicationInviteRepository applicationInviteRepository;

    @Before
    public void setupMockInjection() {
        MockitoAnnotations.initMocks(this);
    }

    private final Long inviteId = 123L;

    @Test
    public void testFindById() {
        ApplicationInviteResource applicationInviteResource = newApplicationInviteResource().withId(inviteId).build();
        ApplicationInvite applicationInvite = newApplicationInvite().withId(inviteId).build();
        when(applicationInviteMapper.mapToResource(applicationInvite)).thenReturn(applicationInviteResource);
        when(applicationInviteRepository.findOne(inviteId)).thenReturn(applicationInvite);
        assertEquals(applicationInviteResource, lookup.getApplicationInviteResource(inviteId));
    }
}
