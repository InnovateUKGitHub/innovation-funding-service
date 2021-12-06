package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserStatus;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.PROFESSIONAL;
import static org.innovateuk.ifs.user.resource.Title.Mr;

public class AssessorProfileResourceDocs {

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