package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.NewUserStagedInviteResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.junit.Assert.assertEquals;

public class NewUserStagedInviteResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedName = "Tom Baldwin";
        String expectedEmail = "tom@poly.io";
        long expectedCompetitionId = 3L;
        long expectedInnovationCategoryId = 5L;

        NewUserStagedInviteResource stagedInviteResource = newNewUserStagedInviteResource()
                .withName(expectedName)
                .withEmail(expectedEmail)
                .withCompetitionId(expectedCompetitionId)
                .withInnovationCategoryId(expectedInnovationCategoryId)
                .build();

        assertEquals(expectedName, stagedInviteResource.getName());
        assertEquals(expectedEmail, stagedInviteResource.getEmail());
        assertEquals(expectedCompetitionId, stagedInviteResource.getCompetitionId());
        assertEquals(expectedInnovationCategoryId, stagedInviteResource.getInnovationCategoryId());
    }

    @Test
    public void buildMany() {
        String[] expectedNames = { "Tom Baldwin", "Cari Morton" };
        String[] expectedEmails = { "tom@poly.io", "cari@poly.io" };
        Long[] expectedCompetitionIds = { 7L, 11L };
        Long[] expectedInnovationCategoryIds = { 13L, 17L };

        List<NewUserStagedInviteResource> rejectionReasons = newNewUserStagedInviteResource()
                .withName(expectedNames)
                .withEmail(expectedEmails)
                .withCompetitionId(expectedCompetitionIds)
                .withInnovationCategoryId(expectedInnovationCategoryIds)
                .build(2);

        NewUserStagedInviteResource first = rejectionReasons.get(0);
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals((long) expectedCompetitionIds[0], first.getCompetitionId());
        assertEquals((long) expectedInnovationCategoryIds[0], first.getInnovationCategoryId());

        NewUserStagedInviteResource second = rejectionReasons.get(1);
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals((long) expectedCompetitionIds[1], second.getCompetitionId());
        assertEquals((long) expectedInnovationCategoryIds[1], second.getInnovationCategoryId());
    }
}
