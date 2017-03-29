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

        Agreement agreement = newAgreement()
                .withId(expectedId)
                .withCurrent(expectedCurrent)
                .withText(expectedText)
                .build();

        assertEquals(expectedId, agreement.getId());
        assertEquals(expectedCurrent, agreement.isCurrent());
        assertEquals(expectedText, agreement.getText());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Boolean[] expectedCurrents = {true, false};
        String[] expectedTexts = {"text1", "text2"};

        List<Agreement> agreements = newAgreement()
                .withId(expectedIds)
                .withCurrent(expectedCurrents)
                .withText(expectedTexts)
                .build(2);

        Agreement first = agreements.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedCurrents[0], first.isCurrent());
        assertEquals(expectedTexts[0], first.getText());

        Agreement second = agreements.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedCurrents[1], second.isCurrent());
        assertEquals(expectedTexts[1], second.getText());
    }
}
