package com.worth.ifs.documentation;

import com.worth.ifs.user.builder.ProfileSkillsResourceBuilder;
import com.worth.ifs.user.resource.BusinessType;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
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
