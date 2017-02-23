package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.NewUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.NewUserStagedInviteResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteListResourceBuilder.newNewUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.junit.Assert.assertEquals;

public class NewUserStagedInviteListResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedCompetitionId = 3L;
        long expectedInnovationCategoryId = 5L;

        List<NewUserStagedInviteResource> expectedInvites = newNewUserStagedInviteResource()
                .withName("Tester 1", "Tester2")
                .withEmail("test1@test.com", "test2@test.com")
                .withCompetitionId(expectedCompetitionId)
                .withInnovationAreaId(expectedInnovationCategoryId)
                .build(2);

        NewUserStagedInviteListResource newUserInviteList = newNewUserStagedInviteListResource()
                .withInvites(expectedInvites)
                .build();

        assertEquals(2, newUserInviteList.getInvites().size());
        assertEquals(expectedInvites.get(0), newUserInviteList.getInvites().get(0));
        assertEquals(expectedInvites.get(1), newUserInviteList.getInvites().get(1));
    }

    @Test
    public void buildMany() {
        List<NewUserStagedInviteResource> expectedInvites1 = newNewUserStagedInviteResource()
                .withName("Tester 1", "Tester2")
                .withEmail("test1@test.com", "test2@test.com")
                .withCompetitionId(7L, 11L)
                .withInnovationAreaId(13L, 17L)
                .build(2);
        List<NewUserStagedInviteResource> expectedInvites2 = newNewUserStagedInviteResource()
                .withName("Tester 3", "Tester4")
                .withEmail("test3@test.com", "test4@test.com")
                .withCompetitionId(20L, 25L)
                .withInnovationAreaId(10L, 11L)
                .build(2);

        List<NewUserStagedInviteListResource> newUserInviteLists = newNewUserStagedInviteListResource()
                .withInvites(expectedInvites1, expectedInvites2)
                .build(2);

        assertEquals(2, newUserInviteLists.size());
        assertEquals(expectedInvites1, newUserInviteLists.get(0).getInvites());
        assertEquals(expectedInvites2, newUserInviteLists.get(1).getInvites());
    }
}
