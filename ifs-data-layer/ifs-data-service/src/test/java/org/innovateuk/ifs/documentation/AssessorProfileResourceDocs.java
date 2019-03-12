package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.PROFESSIONAL;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessorProfileResourceDocs {
    public static final FieldDescriptor[] assessorProfileResourceFields = {
            fieldWithPath("user.id").description("Id of the user").optional(),
            fieldWithPath("user.uid").description("Unique id returned from the authentication identity provider when the user is created").optional(),
            fieldWithPath("user.title").description("Title of the user").optional(),
            fieldWithPath("user.firstName").description("First name of the user").optional(),
            fieldWithPath("user.lastName").description("Last name of the user").optional(),
            fieldWithPath("user.inviteName").description("Not used").optional(),
            fieldWithPath("user.phoneNumber").description("Telephone number of the user").optional(),
            fieldWithPath("user.imageUrl").description("Not used").optional(),
            fieldWithPath("user.email").description("Email address of the user").optional(),
            fieldWithPath("user.password").description("Password of the user").optional(),
            fieldWithPath("user.status").description("Status of the user").optional(),
            fieldWithPath("user.roles").description("Roles that the user is associated with").optional(),
            fieldWithPath("user.allowMarketingEmails").description("allow marketing emails").optional(),
            fieldWithPath("user.profileId").description("Profile id of the user").optional(),
            fieldWithPath("user.inviteName").description("Invite name of the user").optional(),
            fieldWithPath("user.termsAndConditionsIds").description("Ids of accepted terms and conditions").optional(),
            fieldWithPath("user.createdBy").description("User who created this user").optional(),
            fieldWithPath("user.createdOn").description("When the user was created").optional(),
            fieldWithPath("user.modifiedBy").description("User who modified this user").optional(),
            fieldWithPath("user.modifiedOn").description("When the user was modified").optional(),
            fieldWithPath("profile.address").description("Address of the user").optional(),
            fieldWithPath("profile.innovationAreas").description("Innovation areas for the user").optional(),
            fieldWithPath("profile.businessType").description("Business type of assessor").optional(),
            fieldWithPath("profile.skillsAreas").description("Skills areas for the assessor").optional(),
            fieldWithPath("profile.affiliations").description("Affiliations for the assessor").optional()
    };

    public static final AssessorProfileResourceBuilder assessorProfileResourceBuilder = newAssessorProfileResource()
            .withUser(
                    newUserResource()
                            .withTitle(Mr)
                            .withUid("abcdefg")
                            .withFirstName("First")
                            .withLastName("Last")
                            .withEmail("test@test.com")
                            .withPhoneNumber("012434 567890")
                            .withProfile(2L)
                            .withStatus(UserStatus.ACTIVE)
                            .withInviteName("First Last")
                            .build()
            )
            .withProfile(
                    newProfileResource()
                            .withAddress(
                                    newAddressResource()
                                            .withAddressLine1("Electric Works")
                                            .withTown("Sheffield")
                                            .withPostcode("S1 2BJ")
                                            .build()
                            )
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
                            .build()
            );
}