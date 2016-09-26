package com.worth.ifs.user.formatter;

import com.worth.ifs.user.resource.EthnicityResource;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * Formats {@link EthnicityResource} objects for display.
 */
public class EthnicityFormatter implements Formatter<EthnicityResource> {

    @Override
    public EthnicityResource parse(String text, Locale locale) throws ParseException {
        EthnicityResource ethnicityResource = new EthnicityResource();
        ethnicityResource.setId(Long.valueOf(text));
        return ethnicityResource;
    }

    @Override
    public String print(EthnicityResource object, Locale locale) {
        return object != null ? object.getId().toString() : "";
    }
}