package com.worth.ifs.documentation;

import com.worth.ifs.user.builder.AffiliationResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static com.worth.ifs.user.resource.AffiliationType.PERSONAL;
import static java.lang.Boolean.TRUE;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Helper for Spring REST Docs, specifically for affiliations.
 */
public class AffiliationDocs {

    public static final FieldDescriptor[] affiliationResourceFields = {
            fieldWithPath("id").description("id of the affiliation"),
            fieldWithPath("user").description("id of the user that the affiliation is associated with"),
            fieldWithPath("affiliationType").description("the type of the relationship of the affiliation to the user"),
            fieldWithPath("exists").description("flag to signify whether this type of affiliation exists or not"),
            fieldWithPath("relation").description("relationship of the family member to the user, used only for describing family affiliations"),
            fieldWithPath("organisation").description("organisation name, used to describe the principal employer of a user, personal appointments, and family affiliations"),
            fieldWithPath("position").description("name of the position held within an organisation, used to describe the principal employer of a user, personal appointments, and family affiliations"),
            fieldWithPath("description").description("text description, used only for describing professional affiliations, other financial interests, and other family financial interests")
    };

    public static final AffiliationResourceBuilder affiliationResourceBuilder = newAffiliationResource()
            .withId(1L)
            .withUser(1L)
            .withAffiliationType(PERSONAL)
            .withExists(TRUE)
            .withOrganisation("Big Name Corporation")
            .withPosition("Financial Accountant");
}