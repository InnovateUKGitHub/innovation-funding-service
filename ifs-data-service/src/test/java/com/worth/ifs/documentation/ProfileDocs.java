package com.worth.ifs.documentation;

import com.worth.ifs.user.builder.ProfileResourceBuilder;
import com.worth.ifs.user.resource.BusinessType;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDateTime;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static com.worth.ifs.user.builder.ProfileResourceBuilder.newProfileResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProfileDocs {
    public static final FieldDescriptor[] profileResourceFields = {
            fieldWithPath("id").description("The profile identifier"),
            fieldWithPath("user").description("Assessor user associated with the profile"),
            fieldWithPath("address").description("Address of the user"),
            fieldWithPath("skillsAreas").description("Skills of the user"),
            fieldWithPath("businessType").description("Assessor type (business or academic)"),
            fieldWithPath("contract").description("Assessor contract information"),
            fieldWithPath("contractSignedDate").description("Date when contract was signed")
    };

    public static final ProfileResourceBuilder profileResourceBuilder = newProfileResource()
            .withId(1L)
            .withUser(newUserResource().build())
            .withAddress(newAddressResource().build())
            .withSkillsAreas("skills")
            .withBusinessType(BusinessType.BUSINESS)
            .withContract(newContractResource().build())
            .withContractSignedDate(LocalDateTime.now());
}
