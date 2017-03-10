package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.user.domain.Agreement;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.user.builder.AgreementBuilder.newAgreement;
import static org.junit.Assert.assertEquals;

public class AgreementBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        boolean expectedCurrent = true;
        String expectedText = "text";
        String expectedAnnexA = "annexA";
        String expectedAnnexB = "annexB";
        String expectedAnnexC = "annexC";

        Agreement agreement = newAgreement()
                .withId(expectedId)
                .withCurrent(expectedCurrent)
                .withText(expectedText)
                .withAnnexA(expectedAnnexA)
                .withAnnexB(expectedAnnexB)
                .withAnnexC(expectedAnnexC)
                .build();

        assertEquals(expectedId, agreement.getId());
        assertEquals(expectedCurrent, agreement.isCurrent());
        assertEquals(expectedText, agreement.getText());
        assertEquals(expectedAnnexA, agreement.getAnnexA());
        assertEquals(expectedAnnexB, agreement.getAnnexB());
        assertEquals(expectedAnnexC, agreement.getAnnexC());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Boolean[] expectedCurrents = {true, false};
        String[] expectedTexts = {"text1", "text2"};
        String[] expectedAnnexAs = {"annexA1", "annexA2"};
        String[] expectedAnnexBs = {"annexB1", "annexB2"};
        String[] expectedAnnexCs = {"annexC1", "annexC2"};

        List<Agreement> agreements = newAgreement()
                .withId(expectedIds)
                .withCurrent(expectedCurrents)
                .withText(expectedTexts)
                .withAnnexA(expectedAnnexAs)
                .withAnnexB(expectedAnnexBs)
                .withAnnexC(expectedAnnexCs)
                .build(2);

        Agreement first = agreements.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedCurrents[0], first.isCurrent());
        assertEquals(expectedTexts[0], first.getText());
        assertEquals(expectedAnnexAs[0], first.getAnnexA());
        assertEquals(expectedAnnexBs[0], first.getAnnexB());
        assertEquals(expectedAnnexCs[0], first.getAnnexC());

        Agreement second = agreements.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedCurrents[1], second.isCurrent());
        assertEquals(expectedTexts[1], second.getText());
        assertEquals(expectedAnnexAs[1], second.getAnnexA());
        assertEquals(expectedAnnexBs[1], second.getAnnexB());
        assertEquals(expectedAnnexCs[1], second.getAnnexC());
    }
}
