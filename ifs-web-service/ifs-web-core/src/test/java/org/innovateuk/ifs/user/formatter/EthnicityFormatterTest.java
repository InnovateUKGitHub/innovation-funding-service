package org.innovateuk.ifs.user.formatter;

import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.junit.Test;

import java.util.Locale;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
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
