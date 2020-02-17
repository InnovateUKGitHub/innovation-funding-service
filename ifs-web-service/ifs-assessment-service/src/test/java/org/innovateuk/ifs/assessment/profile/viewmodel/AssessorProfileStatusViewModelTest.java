package org.innovateuk.ifs.assessment.profile.viewmodel;

import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessorProfileStatusViewModelTest {

    private AssessorProfileStatusViewModel nothingCompleteProfileStatus;
    private AssessorProfileStatusViewModel skillsCompleteProfileStatus;
    private AssessorProfileStatusViewModel affiliationsCompleteProfileStatus;
    private AssessorProfileStatusViewModel agreementCompleteProfileStatus;
    private AssessorProfileStatusViewModel allCompleteProfileStatus;

    @Before
    public void setUp() {
        nothingCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().build(), RoleProfileState.ACTIVE);
        skillsCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().withSkillsComplete(true).build(), RoleProfileState.ACTIVE);
        affiliationsCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().withAffliliationsComplete(true).build(), RoleProfileState.ACTIVE);
        agreementCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().withAgreementComplete(true).build(), RoleProfileState.ACTIVE);
        allCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource()
                .withSkillsComplete(true)
                .withAffliliationsComplete(true)
                .withAgreementComplete(true)
                .build(), RoleProfileState.ACTIVE);
    }

    @Test
    public void isSkillsComplete() {
        assertFalse(nothingCompleteProfileStatus.isSkillsComplete());
        assertTrue(skillsCompleteProfileStatus.isSkillsComplete());
        assertFalse(affiliationsCompleteProfileStatus.isSkillsComplete());
        assertFalse(agreementCompleteProfileStatus.isSkillsComplete());
        assertTrue(allCompleteProfileStatus.isSkillsComplete());
    }

    @Test
    public void isAffiliationsComplete() {
        assertFalse(nothingCompleteProfileStatus.isAffiliationsComplete());
        assertFalse(skillsCompleteProfileStatus.isAffiliationsComplete());
        assertTrue(affiliationsCompleteProfileStatus.isAffiliationsComplete());
        assertFalse(agreementCompleteProfileStatus.isAffiliationsComplete());
        assertTrue(allCompleteProfileStatus.isAffiliationsComplete());
    }

    @Test
    public void isAgreementComplete() {
        assertFalse(nothingCompleteProfileStatus.isAgreementComplete());
        assertFalse(skillsCompleteProfileStatus.isAgreementComplete());
        assertFalse(affiliationsCompleteProfileStatus.isAgreementComplete());
        assertTrue(agreementCompleteProfileStatus.isAgreementComplete());
        assertTrue(allCompleteProfileStatus.isAgreementComplete());
    }

    @Test
    public void isComplete() {
        assertFalse(nothingCompleteProfileStatus.isComplete());
        assertFalse(skillsCompleteProfileStatus.isComplete());
        assertFalse(affiliationsCompleteProfileStatus.isComplete());
        assertFalse(agreementCompleteProfileStatus.isComplete());
        assertTrue(allCompleteProfileStatus.isComplete());
    }
}
