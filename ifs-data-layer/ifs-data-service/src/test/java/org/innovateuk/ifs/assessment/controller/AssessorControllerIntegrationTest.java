package org.innovateuk.ifs.assessment.controller;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.Affiliation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.AffiliationMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.resource.AffiliationType.PROFESSIONAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AssessorControllerIntegrationTest extends BaseControllerIntegrationTest<AssessorController> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AffiliationMapper affiliationMapper;

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
                .with(id(null))
                .build();
        profileRepository.save(profile);

        List<Affiliation> affiliations = newAffiliation()
                .with(id(null))
                .withExists(true)
                .withAffiliationType(PROFESSIONAL)
                .withUser(user)
                .build(1);
        user.setProfileId(profile.getId());
        user.setAffiliations(affiliations);
        user = userRepository.save(user);

        UserResource userRes = userMapper.mapToResource(user);

        flushAndClearSession();

        //copy fields that are set during save
        userRes.setModifiedOn(user.getModifiedOn());
        userRes.setModifiedBy(userRepository.findByEmail(getCompAdmin().getEmail()).get().getName());

        AssessorProfileResource expectedAssessorProfileResource = newAssessorProfileResource()
                .withUser(userRes)
                .withProfile(
                        newProfileResource()
                                .withAffiliations(affiliationMapper.mapToResource(user.getAffiliations()))
                                .withAddress(newAddressResource().with(id(null)).build())
                                .build()
                )
                .build();

        AssessorProfileResource actualAssessorProfileResource = controller.getAssessorProfile(3L).getSuccessObjectOrThrowException();

        // We can be certain of how everything should be, with the exception of the modified time on the user which
        // we don't know.
        Assert.assertEquals(expectedAssessorProfileResource.getProfile(), actualAssessorProfileResource.getProfile());
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expectedAssessorProfileResource.getUser(), actualAssessorProfileResource.getUser(), "modifiedOn"));
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
