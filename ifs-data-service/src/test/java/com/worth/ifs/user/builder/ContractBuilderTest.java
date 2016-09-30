package com.worth.ifs.user.builder;

import com.worth.ifs.user.domain.Contract;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.user.builder.ContractBuilder.newContract;
import static org.junit.Assert.*;

public class ContractBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        boolean expectedCurrent = true;
        String expectedText = "text";
        String expectedAnnexOne = "annex1";
        String expectedAnnexTwo = "annex2";
        String expectedAnnexThree = "annex3";

        Contract contract = newContract()
                .withId(expectedId)
                .withCurrent(expectedCurrent)
                .withText(expectedText)
                .withAnnexOne(expectedAnnexOne)
                .withAnnexTwo(expectedAnnexTwo)
                .withAnnexThree(expectedAnnexThree)
                .build();

        assertEquals(expectedId, contract.getId());
        assertEquals(expectedCurrent, contract.isCurrent());
        assertEquals(expectedText, contract.getText());
        assertEquals(expectedAnnexOne, contract.getAnnexOne());
        assertEquals(expectedAnnexTwo, contract.getAnnexTwo());
        assertEquals(expectedAnnexThree, contract.getAnnexThree());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = { 1L, 2L };
        Boolean[] expectedCurrents = { true, false };
        String[] expectedTexts = { "text1", "text2" };
        String[] expectedAnnexOnes = { "annex11", "annex12" };
        String[] expectedAnnexTwos = { "annex21", "annex22" };
        String[] expectedAnnexThrees = { "annex31", "annex32" };

        List<Contract> contracts = newContract()
                .withId(expectedIds)
                .withCurrent(expectedCurrents)
                .withText(expectedTexts)
                .withAnnexOne(expectedAnnexOnes)
                .withAnnexTwo(expectedAnnexTwos)
                .withAnnexThree(expectedAnnexThrees)
                .build(2);

        Contract first = contracts.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedCurrents[0], first.isCurrent());
        assertEquals(expectedTexts[0], first.getText());
        assertEquals(expectedAnnexOnes[0], first.getAnnexOne());
        assertEquals(expectedAnnexTwos[0], first.getAnnexTwo());
        assertEquals(expectedAnnexThrees[0], first.getAnnexThree());

        Contract second = contracts.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedCurrents[1], second.isCurrent());
        assertEquals(expectedTexts[1], second.getText());
        assertEquals(expectedAnnexOnes[1], second.getAnnexOne());
        assertEquals(expectedAnnexTwos[1], second.getAnnexTwo());
        assertEquals(expectedAnnexThrees[1], second.getAnnexThree());
    }
}