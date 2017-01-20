package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.domain.Affiliation;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.resource.AffiliationType.PROFESSIONAL;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AssessorControllerIntegrationTest extends BaseControllerIntegrationTest<AssessorController> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Override
    protected void setControllerUnderTest(AssessorController controller) {
        this.controller = controller;
    }

    @Test
    public void getAssessorProfile() throws Exception {
        loginCompAdmin();

        User user = userRepository.findOne(3L);

        Profile profile = newProfile()
                .withSkillsAreas("Testing Skills Area")
                .withBusinessType(ACADEMIC)
                .build();
        List<Affiliation> affiliations = newAffiliation()
                .withExists(true)
                .withOrganisation("University of Nowhere")
                .withAffiliationType(PROFESSIONAL)
                .withPosition("Head of Debating")
                .withUser(user)
                .build(1);

//        user.setProfile(profile);
        user.setAffiliations(affiliations);

        userRepository.save(user);
        flushAndClearSession();

        RestResult<AssessorProfileResource> restResult = controller.getAssessorProfile(3L);

        assertTrue(restResult.isSuccess());

        AssessorProfileResource assessorProfileResource = restResult.getSuccessObjectOrThrowException();

        assertEquals("Professor", assessorProfileResource.getUser().getFirstName());
        assertEquals("Plum", assessorProfileResource.getUser().getLastName());

        assertEquals("Testing Skills Area", assessorProfileResource.getProfile().getSkillsAreas());
        assertEquals(ACADEMIC, assessorProfileResource.getProfile().getBusinessType());

        List<AffiliationResource> affiliationResources = assessorProfileResource.getProfile().getAffiliations();
        assertEquals(1, affiliationResources.size());
        assertEquals(true, affiliationResources.get(0).getExists());
        assertEquals("University of Nowhere", affiliationResources.get(0).getOrganisation());
        assertEquals(PROFESSIONAL, affiliationResources.get(0).getAffiliationType());
        assertEquals("Head of Debating", affiliationResources.get(0).getPosition());

        assertEquals(emptyList(), assessorProfileResource.getProfile().getInnovationAreas());
    }

    @Test
    public void getAssessorProfile_notFound() throws Exception {
        loginCompAdmin();

        RestResult<AssessorProfileResource> restResult = controller.getAssessorProfile(1000L);

        assertTrue(restResult.isFailure());
        assertEquals(notFoundError(User.class, 1000L), restResult.getErrors().get(0));
    }

    @Test
    public void getAssessorProfile_wrongRole() throws Exception {
        loginCompAdmin();

        RestResult<AssessorProfileResource> restResult = controller.getAssessorProfile(1L);

        assertTrue(restResult.isFailure());
        assertEquals(notFoundError(User.class, 1L), restResult.getErrors().get(0));
    }
}
