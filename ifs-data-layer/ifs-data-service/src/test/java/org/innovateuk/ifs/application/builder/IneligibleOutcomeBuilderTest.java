package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.IneligibleOutcomeBuilder.newIneligibleOutcome;
import static org.junit.Assert.assertEquals;


public class IneligibleOutcomeBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        String expectedReason = "Reason";

        IneligibleOutcome ineligibleOutcome = newIneligibleOutcome()
                .withId(expectedId)
                .withReason(expectedReason)
                .build();

        assertEquals(expectedId, ineligibleOutcome.getId());
        assertEquals(expectedReason, ineligibleOutcome.getReason());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedReasons = {"Reason 1", "Reason 2"};

        List<IneligibleOutcome> ineligibleOutcomes = newIneligibleOutcome()
                .withId(expectedIds)
                .withReason(expectedReasons)
                .build(2);

        IneligibleOutcome first = ineligibleOutcomes.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedReasons[0], first.getReason());

        IneligibleOutcome second = ineligibleOutcomes.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedReasons[1], second.getReason());
    }

}