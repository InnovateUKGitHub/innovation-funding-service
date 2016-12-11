package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProfileSkillsDocs {
    public static final FieldDescriptor[] profileSkillsResourceFields = {
            fieldWithPath("user").description("Assessor user associated with the profile skills"),
            fieldWithPath("skillsAreas").description("Skills of the user"),
            fieldWithPath("businessType").description("Assessor type (business or academic)"),
    };

    public static final ProfileSkillsResourceBuilder profileSkillsResourceBuilder = newProfileSkillsResource()
            .withUser(1L)
            .withSkillsAreas("skills")
            .withBusinessType(BusinessType.BUSINESS);
}
