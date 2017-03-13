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
        String expectedAnnexA = "annexA";
        String expectedAnnexB = "annexB";
        String expectedAnnexC = "annexC";

        AgreementResource agreementResource = newAgreementResource()
                .withId(expectedId)
                .withCurrent(expectedCurrent)
                .withText(expectedText)
                .withAnnexA(expectedAnnexA)
                .withAnnexB(expectedAnnexB)
                .withAnnexC(expectedAnnexC)
                .build();

        assertEquals(expectedId, agreementResource.getId());
        assertEquals(expectedCurrent, agreementResource.isCurrent());
        assertEquals(expectedText, agreementResource.getText());
        assertEquals(expectedAnnexA, agreementResource.getAnnexA());
        assertEquals(expectedAnnexB, agreementResource.getAnnexB());
        assertEquals(expectedAnnexC, agreementResource.getAnnexC());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Boolean[] expectedCurrents = {true, false};
        String[] expectedTexts = {"text1", "text2"};
        String[] expectedAnnexAs = {"annexA1", "annexA2"};
        String[] expectedAnnexBs = {"annexB1", "annexB2"};
        String[] expectedAnnexCs = {"annexC1", "annexC2"};

        List<AgreementResource> agreementResources = newAgreementResource()
                .withId(expectedIds)
                .withCurrent(expectedCurrents)
                .withText(expectedTexts)
                .withAnnexA(expectedAnnexAs)
                .withAnnexB(expectedAnnexBs)
                .withAnnexC(expectedAnnexCs)
                .build(2);

        AgreementResource first = agreementResources.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedCurrents[0], first.isCurrent());
        assertEquals(expectedTexts[0], first.getText());
        assertEquals(expectedAnnexAs[0], first.getAnnexA());
        assertEquals(expectedAnnexBs[0], first.getAnnexB());
        assertEquals(expectedAnnexCs[0], first.getAnnexC());

        AgreementResource second = agreementResources.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedCurrents[1], second.isCurrent());
        assertEquals(expectedTexts[1], second.getText());
        assertEquals(expectedAnnexAs[1], second.getAnnexA());
        assertEquals(expectedAnnexBs[1], second.getAnnexB());
        assertEquals(expectedAnnexCs[1], second.getAnnexC());
    }
}
