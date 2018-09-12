package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.CompetitionInviteStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionInviteStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedInvited = 1;
        int expectedAccepted = 2;
        int expectedDeclined = 3;
        int expectedInviteList = 4;
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
        Integer[] expectedInvited = {1, 9};
        Integer[] expectedAccepted = {2, 8};
        Integer[] expectedDeclined = {3, 7};
        Integer[] expectedInviteList = {4, 6};
        List<CompetitionInviteStatisticsResource> competitionInviteStatisticsResources = newCompetitionInviteStatisticsResource()
                .withInvited(expectedInvited)
                .withAccepted(expectedAccepted)
                .withInviteList(expectedInviteList)
                .withDeclined(expectedDeclined)
                .build(2);

        for (int i = 0; i < 2; i++) {
            CompetitionInviteStatisticsResource test = competitionInviteStatisticsResources.get(i);
            assertEquals((int) expectedAccepted[i], test.getAccepted());
            assertEquals((int) expectedDeclined[i], test.getDeclined());
            assertEquals((int) expectedInvited[i], test.getInvited());
            assertEquals((int) expectedInviteList[i], test.getInviteList());
        }
    }
}
