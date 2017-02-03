package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.CompetitionInviteStatisticsResource;
import org.junit.Test;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionInviteStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedInvited = 1L;
        long expectedAccepted = 2L;
        long expectedDeclined = 3L;
        long expectedInviteList = 4L;
        CompetitionInviteStatisticsResource competitionInviteStatisticsResource = newCompetitionInviteStatisticsResource()
                .withInvited(expectedInvited)
                .withAccepted(expectedAccepted)
                .withInviteList(expectedInviteList)
                .withDeclined(expectedDeclined)
                .build();

        assertEquals(expectedAccepted, competitionInviteStatisticsResource.getAccepted());
        assertEquals(expectedDeclined, competitionInviteStatisticsResource.getDeclined());
        assertEquals(expectedInvited, competitionInviteStatisticsResource.getInvited());
        assertEquals(expectedInviteList, competitionInviteStatisticsResource.getInviteList());
    }

    @Test
    public void buildMany() {
        Long[] expectedInvited = {1L, 9L};
        Long[] expectedAccepted = {2L, 8L};
        Long[] expectedDeclined = {3L, 7L};
        Long[] expectedInviteList = {4L, 6L};
        List<CompetitionInviteStatisticsResource> competitionInviteStatisticsResources = newCompetitionInviteStatisticsResource()
                .withInvited(expectedInvited)
                .withAccepted(expectedAccepted)
                .withInviteList(expectedInviteList)
                .withDeclined(expectedDeclined)
                .build(2);

        for (int i = 0; i < 2; i++) {
            CompetitionInviteStatisticsResource test = competitionInviteStatisticsResources.get(i);
            assertEquals((long) expectedAccepted[i], test.getAccepted());
            assertEquals((long) expectedDeclined[i], test.getDeclined());
            assertEquals((long) expectedInvited[i], test.getInvited());
            assertEquals((long) expectedInviteList[i], test.getInviteList());
        }
    }
}
