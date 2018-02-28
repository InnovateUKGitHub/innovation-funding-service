package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.StagedApplicationListResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.StagedApplicationListResourceBuilder.newStagedApplicationListResource;
import static org.innovateuk.ifs.invite.builder.StagedApplicationResourceBuilder.newStagedApplicationResource;
import static org.junit.Assert.assertEquals;

public class StagedApplicationListResourceBuilderTest {

    @Test
    public void buildOne() {
        List<StagedApplicationResource> expectedApplications = newStagedApplicationResource()
                .withCompetitionId(2L, 3L)
                .withApplicationId(5L, 7L)
                .build(2);

        StagedApplicationListResource stagedApplicationList = newStagedApplicationListResource()
                .withInvites(expectedApplications)
                .build();

        assertEquals(expectedApplications.size(), stagedApplicationList.getInvites().size());
        assertEquals(expectedApplications.get(0), stagedApplicationList.getInvites().get(0));
        assertEquals(expectedApplications.get(1), stagedApplicationList.getInvites().get(1));
    }

    @Test
    public void buildMany() {
        List<StagedApplicationResource> expectedApplications1 = newStagedApplicationResource()
                .withCompetitionId(2L, 3L)
                .withApplicationId(5L, 7L)
                .build(2);
        List<StagedApplicationResource> expectedApplications2 = newStagedApplicationResource()
                .withCompetitionId(11L, 13L)
                .withApplicationId(17L, 19L)
                .build(2);

        List<StagedApplicationListResource> stagedApplicationLists = newStagedApplicationListResource()
                .withInvites(expectedApplications1, expectedApplications2)
                .build(2);

        assertEquals(2, stagedApplicationLists.size());
        assertEquals(expectedApplications1, stagedApplicationLists.get(0).getInvites());
        assertEquals(expectedApplications2, stagedApplicationLists.get(1).getInvites());
    }
}