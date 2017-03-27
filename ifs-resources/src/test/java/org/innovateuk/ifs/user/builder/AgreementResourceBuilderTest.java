package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.user.resource.AgreementResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.user.builder.AgreementResourceBuilder.newAgreementResource;
import static org.junit.Assert.assertEquals;

public class AgreementResourceBuilderTest {
    @Test
    public void buildOne() {
        Long expectedId = 1L;
        boolean expectedCurrent = true;
        String expectedText = "text";

        AgreementResource agreementResource = newAgreementResource()
                .withId(expectedId)
                .withCurrent(expectedCurrent)
                .withText(expectedText)
                .build();

        assertEquals(expectedId, agreementResource.getId());
        assertEquals(expectedCurrent, agreementResource.isCurrent());
        assertEquals(expectedText, agreementResource.getText());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Boolean[] expectedCurrents = {true, false};
        String[] expectedTexts = {"text1", "text2"};

        List<AgreementResource> agreementResources = newAgreementResource()
                .withId(expectedIds)
                .withCurrent(expectedCurrents)
                .withText(expectedTexts)
                .build(2);

        AgreementResource first = agreementResources.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedCurrents[0], first.isCurrent());
        assertEquals(expectedTexts[0], first.getText());

        AgreementResource second = agreementResources.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedCurrents[1], second.isCurrent());
        assertEquals(expectedTexts[1], second.getText());
    }
}
