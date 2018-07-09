package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder;
import org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class TermsAndConditionsResourceDocs {

    public static final FieldDescriptor[] termsAndConditionsResourceFields = {
            fieldWithPath("id").description("id of the terms and conditions").optional(),
            fieldWithPath("name").description("name of the terms and conditions"),
            fieldWithPath("template").description("template filename of the terms and conditions"),
            fieldWithPath("version").description("version of the terms and conditions"),
            fieldWithPath("createdBy").description("user who created this terms and conditions"),
            fieldWithPath("createdOn").description("when the terms and conditions was created"),
            fieldWithPath("modifiedBy").description("user who modified this terms and conditions"),
            fieldWithPath("modifiedOn").description("when the terms and conditions was modified")
    };

    public static final GrantTermsAndConditionsResourceBuilder grantTermsAndConditionsResourceBuilder =
            newGrantTermsAndConditionsResource()
                    .withId(1L)
                    .withName("Innovate UK")
                    .withTemplate("default-terms-and-conditions")
                    .withVersion(1)
                    .withCreatedBy("John")
                    .withCreatedOn(ZonedDateTime.now())
                    .withModifiedBy("Doe")
                    .withModifiedOn(ZonedDateTime.now());

    public static final SiteTermsAndConditionsResourceBuilder siteTermsAndConditionsResourceBuilder =
            SiteTermsAndConditionsResourceBuilder
                    .newSiteTermsAndConditionsResource()
                    .withId(1L)
                    .withName("Site terms and conditions")
                    .withTemplate("terms-and-conditions")
                    .withVersion(1)
                    .withCreatedBy("John")
                    .withCreatedOn(ZonedDateTime.now())
                    .withModifiedBy("Doe")
                    .withModifiedOn(ZonedDateTime.now());
}
