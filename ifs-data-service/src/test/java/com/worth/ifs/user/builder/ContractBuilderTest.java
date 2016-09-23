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
        String expectedAppendixOne = "appendix1";
        String expectedAppendixTwo = "appendix2";

        Contract contract = newContract()
                .withId(expectedId)
                .withCurrent(expectedCurrent)
                .withText(expectedText)
                .withAppendixOne(expectedAppendixOne)
                .withAppendixTwo(expectedAppendixTwo)
                .build();

        assertEquals(expectedId, contract.getId());
        assertEquals(expectedCurrent, contract.isCurrent());
        assertEquals(expectedText, contract.getText());
        assertEquals(expectedAppendixOne, contract.getAppendixOne());
        assertEquals(expectedAppendixTwo, contract.getAppendixTwo());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = { 1L, 2L };
        Boolean[] expectedCurrents = { true, false };
        String[] expectedTexts = { "text1", "text2" };
        String[] expectedAppendixOnes = { "appendix11", "appendix12" };
        String[] expectedAppendixTwos = { "appendix21", "appendix22" };

        List<Contract> contracts = newContract()
                .withId(expectedIds)
                .withCurrent(expectedCurrents)
                .withText(expectedTexts)
                .withAppendixOne(expectedAppendixOnes)
                .withAppendixTwo(expectedAppendixTwos)
                .build(2);

        Contract first = contracts.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedCurrents[0], first.isCurrent());
        assertEquals(expectedTexts[0], first.getText());
        assertEquals(expectedAppendixOnes[0], first.getAppendixOne());
        assertEquals(expectedAppendixTwos[0], first.getAppendixTwo());

        Contract second = contracts.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedCurrents[1], second.isCurrent());
        assertEquals(expectedTexts[1], second.getText());
        assertEquals(expectedAppendixOnes[1], second.getAppendixOne());
        assertEquals(expectedAppendixTwos[1], second.getAppendixTwo());
    }
}