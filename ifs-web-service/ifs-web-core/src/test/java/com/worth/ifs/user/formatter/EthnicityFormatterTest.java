package com.worth.ifs.user.formatter;

import com.worth.ifs.user.resource.EthnicityResource;
import org.junit.Test;

import java.util.Locale;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.junit.Assert.assertEquals;

public class EthnicityFormatterTest {

    @Test
    public void parse() throws Exception {
        EthnicityFormatter formatter = new EthnicityFormatter();

        String text = "1";
        EthnicityResource expected = new EthnicityResource();
        expected.setId(1L);

        assertEquals(expected, formatter.parse(text, Locale.UK));
    }

    @Test
    public void print() throws Exception {
        EthnicityFormatter formatter = new EthnicityFormatter();

        EthnicityResource ethnicity = newEthnicityResource()
                .with(id(1L))
                .withName("Name")
                .withDescription("Description")
                .withPriority(1)
                .build();

        assertEquals("1", formatter.print(ethnicity, Locale.UK));
    }
}