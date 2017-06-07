package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.junit.Assert.assertEquals;

public class ExistingUserStagedInviteResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedUserId = 1L;
        String expectedEmail = "tom@poly.io";
        long expectedCompetitionId = 3L;

        ExistingUserStagedInviteResource stagedInviteResource = newExistingUserStagedInviteResource()
                .withUserId(expectedUserId)
                .withCompetitionId(expectedCompetitionId)
                .build();

        assertEquals(expectedUserId, stagedInviteResource.getUserId());
        assertEquals(expectedCompetitionId, stagedInviteResource.getCompetitionId());
    }

    @Test
    public void buildMany() {
        Long[] expectedUserIds = { 1L, 2L};
        String[] expectedEmails = { "tom@poly.io", "cari@poly.io" };
        Long[] expectedCompetitionIds = { 7L, 11L };

        List<ExistingUserStagedInviteResource> rejectionReasons = newExistingUserStagedInviteResource()
                .withUserId(expectedUserIds)
                .withCompetitionId(expectedCompetitionIds)
                .build(2);

        ExistingUserStagedInviteResource first = rejectionReasons.get(0);
        assertEquals((long) expectedUserIds[0], first.getUserId());
        assertEquals((long) expectedCompetitionIds[0], first.getCompetitionId());

        ExistingUserStagedInviteResource second = rejectionReasons.get(1);
        assertEquals((long) expectedUserIds[1], second.getUserId());
        assertEquals((long) expectedCompetitionIds[1], second.getCompetitionId());
    }
}
