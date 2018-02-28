package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.StagedApplicationResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.StagedApplicationResourceBuilder.newStagedApplicationResource;
import static org.junit.Assert.assertEquals;

public class StagedApplicationResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedApplicationId = 1L;
        long expectedCompetitionId = 2L;

        StagedApplicationResource stagedInviteResource = newStagedApplicationResource()
                .withApplicationId(expectedApplicationId)
                .withCompetitionId(expectedCompetitionId)
                .build();

        assertEquals(expectedApplicationId, stagedInviteResource.getApplicationId());
        assertEquals(expectedCompetitionId, stagedInviteResource.getCompetitionId());
    }

    @Test
    public void buildMany() {
        Long[] expectedApplicationIds = { 3L, 5L};
        Long[] expectedCompetitionIds = { 7L, 11L };

        List<StagedApplicationResource> stagedInviteResources = newStagedApplicationResource()
                .withApplicationId(expectedApplicationIds)
                .withCompetitionId(expectedCompetitionIds)
                .build(2);

        StagedApplicationResource first = stagedInviteResources.get(0);
        assertEquals((long) expectedApplicationIds[0], first.getApplicationId());
        assertEquals((long) expectedCompetitionIds[0], first.getCompetitionId());

        StagedApplicationResource second = stagedInviteResources.get(1);
        assertEquals((long) expectedApplicationIds[1], second.getApplicationId());
        assertEquals((long) expectedCompetitionIds[1], second.getCompetitionId());
    }
}
