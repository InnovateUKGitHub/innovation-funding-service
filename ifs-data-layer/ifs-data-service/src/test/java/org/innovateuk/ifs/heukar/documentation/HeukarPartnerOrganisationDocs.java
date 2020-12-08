package org.innovateuk.ifs.heukar.documentation;

import org.innovateuk.ifs.organisation.builder.HeukarPartnerOrganisationResourceBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.organisation.builder.HeukarPartnerOrganisationResourceBuilder.newHeukarPartnerOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

public class HeukarPartnerOrganisationDocs {

    public static final FieldDescriptor[] heukarPartnerOrganisationFields = {
            fieldWithPath("id").description("Id of the partner organisation").optional(),
            fieldWithPath("applicationId").description("Id of the application").optional(),
            fieldWithPath("organisationTypeResource").description("Type of partner organisation").optional(),
    };

    public static final FieldDescriptor[] organisationTypeFields = {
            fieldWithPath("id").description("Id of the org type").optional(),
            fieldWithPath("name").description("Name of the org type").optional(),
            fieldWithPath("description").description("Description of the org type").optional(),
            fieldWithPath("visibleInSetup").description("Whether the org type is visible in setup").optional(),
            fieldWithPath("parentOrganisationType").description("Parents org type").optional()
    };

    public static final HeukarPartnerOrganisationResourceBuilder heukarParterOrganisationResourceBuilder =
            newHeukarPartnerOrganisationResource()
                    .withOrganisationTypeResource(newOrganisationTypeResource().withId(1L).withName("test").build())
                    .withId(1L)
                    .withApplicationId(1L);

}
