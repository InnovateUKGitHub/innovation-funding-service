package org.innovateuk.ifs.invite.formatter;

import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.junit.Test;

import java.util.Locale;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.junit.Assert.assertEquals;

public class RejectionReasonFormatterTest {

    @Test
    public void parse() throws Exception {
        RejectionReasonFormatter formatter = new RejectionReasonFormatter();

        String text = "1";
        RejectionReasonResource expected = new RejectionReasonResource();
        expected.setId(1L);

        assertEquals(expected, formatter.parse(text, Locale.UK));
    }

    @Test
    public void print() throws Exception {
        RejectionReasonFormatter formatter = new RejectionReasonFormatter();

        RejectionReasonResource reason = newRejectionReasonResource()
                .with(id(1L))
                .withReason("Reason")
                .withActive(TRUE)
                .withPriority(1)
                .build();

        assertEquals("1", formatter.print(reason, Locale.UK));
    }
}
