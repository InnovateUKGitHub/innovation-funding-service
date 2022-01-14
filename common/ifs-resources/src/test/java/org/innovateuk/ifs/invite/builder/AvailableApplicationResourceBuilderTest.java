package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.AvailableApplicationResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.AvailableApplicationResourceBuilder.newAvailableApplicationResource;
import static org.junit.Assert.assertEquals;

public class AvailableApplicationResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedName = "name";
        String expectedLeadOrganisation = "leadOrganisation";

        AvailableApplicationResource availableApplicationResource = newAvailableApplicationResource()
                .withName(expectedName)
                .withLeadOrganisation(expectedLeadOrganisation)
                .build();

        assertEquals(expectedName, availableApplicationResource.getName());
        assertEquals(expectedLeadOrganisation, availableApplicationResource.getLeadOrganisation());
    }

    @Test
    public void buildMany() {
        String[] expectedNames = {"name1", "name2"};
        String[] expectedLeadOrganisations = {"leadOrganisation1", "leadOrganisation2"};

        List<AvailableApplicationResource> availableApplicationResources = newAvailableApplicationResource()
                .withName(expectedNames)
                .withLeadOrganisation(expectedLeadOrganisations)
                .build(2);

        AvailableApplicationResource first = availableApplicationResources.get(0);
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedLeadOrganisations[0], first.getLeadOrganisation());

        AvailableApplicationResource second = availableApplicationResources.get(1);
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedLeadOrganisations[1], second.getLeadOrganisation());
    }
}
