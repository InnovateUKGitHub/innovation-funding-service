package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder;
import org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProfileSkillsDocs {

    public static final FieldDescriptor[] profileSkillsResourceFields = {
            fieldWithPath("user").description("Assessor user associated with the profile skills"),
            fieldWithPath("innovationAreas").description("the Innovation areas of the user"),
            fieldWithPath("skillsAreas").description("Skills of the user"),
            fieldWithPath("businessType").description("Assessor type (business or academic)"),
    };

    public static final FieldDescriptor[] profileSkillsEditResourceFields = {
            fieldWithPath("user").description("Assessor user associated with the profile skills"),
            fieldWithPath("skillsAreas").description("Skills of the user"),
            fieldWithPath("businessType").description("Assessor type (business or academic)"),
    };

    public static final ProfileSkillsResourceBuilder profileSkillsResourceBuilder = newProfileSkillsResource()
            .withUser(1L)
            .withInnovationAreas(
                    newInnovationAreaResource()
                            .withName("Data", "Cyber Security")
                            .withSector(3L)
                            .withSectorName("Emerging and enabling technologies")
                            .build(2))
            .withSkillsAreas("skills")
            .withBusinessType(BusinessType.BUSINESS);

    public static final ProfileSkillsEditResourceBuilder profileSkillsEditResourceBuilder = newProfileSkillsEditResource()
            .withUser(1L)
            .withSkillsAreas("skills")
            .withBusinessType(BusinessType.BUSINESS);
}
