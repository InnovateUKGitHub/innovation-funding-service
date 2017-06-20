package org.innovateuk.ifs.affiliation.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AffiliationControllerIntegrationTest extends BaseControllerIntegrationTest<AffiliationController> {

    @Override
    @Autowired
    protected void setControllerUnderTest(AffiliationController controller) {
        this.controller = controller;
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetUserAffiliations() throws Exception {
        loginPaulPlum();

        User user = userRepository.findOne(getPaulPlum().getId());
        Long userId = user.getId();

        // Save some existing Affiliations
        user.setAffiliations(newAffiliation()
                .withId(null, null)
                .withAffiliationType(EMPLOYER, PERSONAL_FINANCIAL)
                .withExists(TRUE, TRUE)
                .withUser(user, user)
                .build(2));
        userRepository.save(user);

        List<AffiliationResource> response = controller.getUserAffiliations(userId).getSuccessObjectOrThrowException();
        assertEquals(2, response.size());

        assertEquals(EMPLOYER, response.get(0).getAffiliationType());
        assertEquals(PERSONAL_FINANCIAL, response.get(1).getAffiliationType());
    }

    @Test
    public void testUpdateUserAffiliations() throws Exception {
        loginPaulPlum();

        User user = userRepository.findOne(getPaulPlum().getId());
        Long userId = user.getId();

        // Save some existing Affiliations
        user.setAffiliations(newAffiliation()
                .withId(null, null)
                .withAffiliationType(EMPLOYER, PERSONAL_FINANCIAL)
                .withExists(TRUE, TRUE)
                .withUser(user, user)
                .build(2));
        userRepository.save(user);

        List<AffiliationResource> getAfterSaveResponse = controller.getUserAffiliations(userId).getSuccessObjectOrThrowException();
        assertEquals(2, getAfterSaveResponse.size());

        RestResult<Void> updateResponse = controller.updateUserAffiliations(userId, newAffiliationResource()
                .withId(null, null)
                .withAffiliationType(PROFESSIONAL, FAMILY_FINANCIAL)
                .withExists(TRUE, TRUE)
                .withUser(userId, userId)
                .build(2));

        assertTrue(updateResponse.isSuccess());

        List<AffiliationResource> getAfterUpdateResponse = controller.getUserAffiliations(userId).getSuccessObjectOrThrowException();
        assertEquals(2, getAfterUpdateResponse.size());
        assertEquals(PROFESSIONAL, getAfterUpdateResponse.get(0).getAffiliationType());
        assertEquals(FAMILY_FINANCIAL, getAfterUpdateResponse.get(1).getAffiliationType());
    }
}
