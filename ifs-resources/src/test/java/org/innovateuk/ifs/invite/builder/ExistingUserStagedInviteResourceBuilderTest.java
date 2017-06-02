package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.junit.Assert.assertEquals;

public class ExistingUserStagedInviteResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedEmail = "tom@poly.io";
        long expectedCompetitionId = 3L;

        ExistingUserStagedInviteResource stagedInviteResource = newExistingUserStagedInviteResource()
                .withEmail(expectedEmail)
                .withCompetitionId(expectedCompetitionId)
                .build();

        assertEquals(expectedEmail, stagedInviteResource.getEmail());
        assertEquals(expectedCompetitionId, stagedInviteResource.getCompetitionId());
    }

    @Test
    public void buildMany() {
        String[] expectedEmails = { "tom@poly.io", "cari@poly.io" };
        Long[] expectedCompetitionIds = { 7L, 11L };

        List<ExistingUserStagedInviteResource> rejectionReasons = newExistingUserStagedInviteResource()
                .withEmail(expectedEmails)
                .withCompetitionId(expectedCompetitionIds)
                .build(2);

        ExistingUserStagedInviteResource first = rejectionReasons.get(0);
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals((long) expectedCompetitionIds[0], first.getCompetitionId());

        ExistingUserStagedInviteResource second = rejectionReasons.get(1);
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals((long) expectedCompetitionIds[1], second.getCompetitionId());
    }
}
