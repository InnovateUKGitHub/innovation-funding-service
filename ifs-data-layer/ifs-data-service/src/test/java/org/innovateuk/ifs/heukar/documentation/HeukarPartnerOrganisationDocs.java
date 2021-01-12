package org.innovateuk.ifs.heukar.documentation;

import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationTypeEnum;
import org.innovateuk.ifs.organisation.builder.HeukarPartnerOrganisationResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.organisation.builder.HeukarPartnerOrganisationResourceBuilder.newHeukarPartnerOrganisationResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class HeukarPartnerOrganisationDocs {

    public static final FieldDescriptor[] heukarPartnerOrganisationFields = {
            fieldWithPath("id").description("Id of the partner organisation").optional(),
            fieldWithPath("applicationId").description("Id of the application").optional(),
            fieldWithPath("heukarPartnerOrganisationType").description("Type of partner organisation").optional(),
    };

    public static final HeukarPartnerOrganisationResourceBuilder heukarParterOrganisationResourceBuilder =
            newHeukarPartnerOrganisationResource()
                    .withOrganisationTypeResource(HeukarPartnerOrganisationTypeEnum.fromId(1))
                    .withId(1L)
                    .withApplicationId(1L);

}
