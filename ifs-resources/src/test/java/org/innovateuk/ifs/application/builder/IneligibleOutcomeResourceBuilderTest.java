package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.application.builder.IneligibleOutcomeResourceBuilder.newIneligibleOutcomeResource;
import static org.junit.Assert.assertEquals;

public class IneligibleOutcomeResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedReason = "Reason";
        String expectedRemovedBy = "Removed by";
        ZonedDateTime expectedRemovedOn = ZonedDateTime.now();

        IneligibleOutcomeResource ineligibleOutcomeResource = newIneligibleOutcomeResource()
                .withReason(expectedReason)
                .withRemovedBy(expectedRemovedBy)
                .withRemovedOn(expectedRemovedOn)
                .build();

        assertEquals(expectedReason, ineligibleOutcomeResource.getReason());
        assertEquals(expectedRemovedBy, ineligibleOutcomeResource.getRemovedBy());
        assertEquals(expectedRemovedOn, ineligibleOutcomeResource.getRemovedOn());
    }

    @Test
    public void buildMany() {
        String[] expectedReasons = {"Reason 1", "Reason 2"};
        String[] expectedRemovedBys = {"Removed by 1", "Removed by 2"};
        ZonedDateTime[] expectedRemovedOns = {ZonedDateTime.now().minusHours(1), ZonedDateTime.now().plusHours(1)};

        List<IneligibleOutcomeResource> ineligibleOutcomeResources = newIneligibleOutcomeResource()
                .withReason(expectedReasons)
                .withRemovedBy(expectedRemovedBys)
                .withRemovedOn(expectedRemovedOns)
                .build(2);

        IneligibleOutcomeResource first = ineligibleOutcomeResources.get(0);

        assertEquals(expectedReasons[0], first.getReason());
        assertEquals(expectedRemovedBys[0], first.getRemovedBy());
        assertEquals(expectedRemovedOns[0], first.getRemovedOn());

        IneligibleOutcomeResource second = ineligibleOutcomeResources.get(1);

        assertEquals(expectedReasons[1], second.getReason());
        assertEquals(expectedRemovedBys[1], second.getRemovedBy());
        assertEquals(expectedRemovedOns[1], second.getRemovedOn());
    }

}