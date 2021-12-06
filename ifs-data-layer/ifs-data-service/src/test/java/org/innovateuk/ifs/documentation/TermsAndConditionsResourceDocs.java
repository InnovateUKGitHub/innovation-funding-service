package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder;
import org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;

public class TermsAndConditionsResourceDocs {


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
