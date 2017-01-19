package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.Disability;
import org.innovateuk.ifs.user.resource.Gender;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.PROFESSIONAL;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessorProfileResourceDocs {
    public static final FieldDescriptor[] assessorProfileResourceFields = {
            fieldWithPath("title").description("title of the user"),
            fieldWithPath("firstName").description("first name of the user"),
            fieldWithPath("lastName").description("last name of the user"),
            fieldWithPath("phoneNumber").description("telephone number of the user"),
            fieldWithPath("gender").description("gender of the user"),
            fieldWithPath("disability").description("disability of the user"),
            fieldWithPath("ethnicity").description("ethnic group of the user"),
            fieldWithPath("address").description("assess of the user"),
            fieldWithPath("email").description("email address of the user"),
            fieldWithPath("innovationAreas").description("innovation areas for the user"),
            fieldWithPath("businessType").description("business type of assessor"),
            fieldWithPath("skillsAreas").description("skills areas for the assessor"),
            fieldWithPath("affiliations").description("affiliations for the assessor")
    };

    public static final AssessorProfileResourceBuilder assessorProfileResourceBuilder = newAssessorProfileResource()
            .withTitle("Mr")
            .withFirstName("First")
            .withLastName("Last")
            .withPhoneNumber("012434 567890")
            .withGender(Gender.MALE)
            .withDisability(Disability.NOT_STATED)
            .withEthnicity(newEthnicityResource().with(id(1L)).build())
            .withAddress(newAddressResource().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build())
            .withSkillsAreas("Forensic analysis")
            .withBusinessType(BusinessType.ACADEMIC)
            .withInnovationAreas(
                    newInnovationAreaResource()
                            .withId(2L, 3L)
                            .withName("Nanochemistry", "Biochemistry")
                            .withSector(1L)
                            .build(2)
            )
            .withAffiliations(
                    newAffiliationResource()
                            .withId(1L)
                            .withUser(1L)
                            .withExists(true)
                            .withAffiliationType(PROFESSIONAL)
                            .withOrganisation("University of Somewhere")
                            .withPosition("Professor")
                            .build(1)
            )
            .withEmail("test@test.com");
}
