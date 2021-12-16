package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.AffiliationListResourceBuilder;
import org.innovateuk.ifs.user.builder.AffiliationResourceBuilder;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.user.builder.AffiliationListResourceBuilder.newAffiliationListResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.PERSONAL;

/**
 * Helper for Spring REST Docs, specifically for affiliations.
 */
public class AffiliationDocs {

    public static final AffiliationResourceBuilder affiliationResourceBuilder = newAffiliationResource()
            .withId(1L)
            .withUser(1L)
            .withAffiliationType(PERSONAL)
            .withExists(TRUE)
            .withOrganisation("Big Name Corporation")
            .withPosition("Financial Accountant");

    public static final AffiliationListResourceBuilder affiliationListResourceBuilder = newAffiliationListResource()
            .withAffiliationList(affiliationResourceBuilder.build(1));
}
