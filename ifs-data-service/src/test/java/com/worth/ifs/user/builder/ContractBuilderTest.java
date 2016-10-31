package com.worth.ifs.user.builder;

import com.worth.ifs.user.domain.Contract;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.user.builder.ContractBuilder.newContract;
import static org.junit.Assert.assertEquals;

public class ContractBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        boolean expectedCurrent = true;
        String expectedText = "text";
        String expectedAnnexA = "annexA";
        String expectedAnnexB = "annexB";
        String expectedAnnexC = "annexC";

        Contract contract = newContract()
                .withId(expectedId)
                .withCurrent(expectedCurrent)
                .withText(expectedText)
                .withAnnexA(expectedAnnexA)
                .withAnnexB(expectedAnnexB)
                .withAnnexC(expectedAnnexC)
                .build();

        assertEquals(expectedId, contract.getId());
        assertEquals(expectedCurrent, contract.isCurrent());
        assertEquals(expectedText, contract.getText());
        assertEquals(expectedAnnexA, contract.getAnnexA());
        assertEquals(expectedAnnexB, contract.getAnnexB());
        assertEquals(expectedAnnexC, contract.getAnnexC());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Boolean[] expectedCurrents = {true, false};
        String[] expectedTexts = {"text1", "text2"};
        String[] expectedAnnexAs = {"annexA1", "annexA2"};
        String[] expectedAnnexBs = {"annexB1", "annexB2"};
        String[] expectedAnnexCs = {"annexC1", "annexC2"};

        List<Contract> contracts = newContract()
                .withId(expectedIds)
                .withCurrent(expectedCurrents)
                .withText(expectedTexts)
                .withAnnexA(expectedAnnexAs)
                .withAnnexB(expectedAnnexBs)
                .withAnnexC(expectedAnnexCs)
                .build(2);

        Contract first = contracts.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedCurrents[0], first.isCurrent());
        assertEquals(expectedTexts[0], first.getText());
        assertEquals(expectedAnnexAs[0], first.getAnnexA());
        assertEquals(expectedAnnexBs[0], first.getAnnexB());
        assertEquals(expectedAnnexCs[0], first.getAnnexC());

        Contract second = contracts.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedCurrents[1], second.isCurrent());
        assertEquals(expectedTexts[1], second.getText());
        assertEquals(expectedAnnexAs[1], second.getAnnexA());
        assertEquals(expectedAnnexBs[1], second.getAnnexB());
        assertEquals(expectedAnnexCs[1], second.getAnnexC());
    }
}