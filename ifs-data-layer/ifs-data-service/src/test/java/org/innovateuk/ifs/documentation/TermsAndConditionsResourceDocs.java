package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.TermsAndConditionsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.competition.builder.TermsAndConditionsResourceBuilder.newTermsAndConditionsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class TermsAndConditionsResourceDocs {

    public static final FieldDescriptor[] termsAndConditionsResourceFields = {
            fieldWithPath("id").description("Id of the TermsAndConditionsResource"),
            fieldWithPath("name").description("name of the terms and conditions"),
            fieldWithPath("template").description("template name of the terms and conditions"),
            fieldWithPath("version").description("version number of the terms and conditions")
    };

    public static final TermsAndConditionsResourceBuilder termsAndConditionsResourceBuilder = newTermsAndConditionsResource()
            .withId(1L)
            .withName("Default Terms and Conditions")
            .withTemplate("default-terms-and-conditions")
            .withVersion("1");
}
