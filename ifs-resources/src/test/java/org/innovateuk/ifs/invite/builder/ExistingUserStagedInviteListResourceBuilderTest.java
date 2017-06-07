package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteListResourceBuilder.newExistingUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.junit.Assert.assertEquals;

public class ExistingUserStagedInviteListResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedCompetitionId = 3L;

        List<ExistingUserStagedInviteResource> expectedInvites = newExistingUserStagedInviteResource()
                .withCompetitionId(expectedCompetitionId)
                .build(2);

        ExistingUserStagedInviteListResource existingUserInviteList = newExistingUserStagedInviteListResource()
                .withInvites(expectedInvites)
                .build();

        assertEquals(2, existingUserInviteList.getInvites().size());
        assertEquals(expectedInvites.get(0), existingUserInviteList.getInvites().get(0));
        assertEquals(expectedInvites.get(1), existingUserInviteList.getInvites().get(1));
    }

    @Test
    public void buildMany() {
        List<ExistingUserStagedInviteResource> expectedInvites1 = newExistingUserStagedInviteResource()
                .withCompetitionId(7L, 11L)
                .build(2);
        List<ExistingUserStagedInviteResource> expectedInvites2 = newExistingUserStagedInviteResource()
                .withCompetitionId(20L, 25L)
                .build(2);

        List<ExistingUserStagedInviteListResource> existingUserInviteLists = newExistingUserStagedInviteListResource()
                .withInvites(expectedInvites1, expectedInvites2)
                .build(2);

        assertEquals(2, existingUserInviteLists.size());
        assertEquals(expectedInvites1, existingUserInviteLists.get(0).getInvites());
        assertEquals(expectedInvites2, existingUserInviteLists.get(1).getInvites());
    }
}
