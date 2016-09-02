package com.worth.ifs.invite.formatter;

import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * Formats {@link RejectionReasonResource} objects for display.
 */
public class RejectionReasonFormatter implements Formatter<RejectionReasonResource> {

    @Override
    public RejectionReasonResource parse(String text, Locale locale) throws ParseException {
        RejectionReasonResource rejectionReasonResource = new RejectionReasonResource();
        rejectionReasonResource.setId(Long.valueOf(text));
        return rejectionReasonResource;
    }

    @Override
    public String print(RejectionReasonResource object, Locale locale) {
        return object != null ? object.getId().toString() : "";
    }
}