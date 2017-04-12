package org.innovateuk.ifs.assessment.profile.viewmodel;

import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileStatusViewModel;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.junit.Assert.*;

public class AssessorProfileStatusViewModelTest {

    private AssessorProfileStatusViewModel nothingCompleteProfileStatus;
    private AssessorProfileStatusViewModel skillsCompleteProfileStatus;
    private AssessorProfileStatusViewModel affiliationsCompleteProfileStatus;
    private AssessorProfileStatusViewModel agreementCompleteProfileStatus;
    private AssessorProfileStatusViewModel allCompleteProfileStatus;

    @Before
    public void setUp() throws Exception {
        nothingCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().build());
        skillsCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().withSkillsComplete(true).build());
        affiliationsCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().withAffliliationsComplete(true).build());
        agreementCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().withAgreementComplete(true).build());
        allCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource()
                .withSkillsComplete(true)
                .withAffliliationsComplete(true)
                .withAgreementComplete(true)
                .build());
    }

    @Test
    public void isSkillsComplete() throws Exception {
        assertFalse(nothingCompleteProfileStatus.isSkillsComplete());
        assertTrue(skillsCompleteProfileStatus.isSkillsComplete());
        assertFalse(affiliationsCompleteProfileStatus.isSkillsComplete());
        assertFalse(agreementCompleteProfileStatus.isSkillsComplete());
        assertTrue(allCompleteProfileStatus.isSkillsComplete());
    }

    @Test
    public void isAffiliationsComplete() throws Exception {
        assertFalse(nothingCompleteProfileStatus.isAffiliationsComplete());
        assertFalse(skillsCompleteProfileStatus.isAffiliationsComplete());
        assertTrue(affiliationsCompleteProfileStatus.isAffiliationsComplete());
        assertFalse(agreementCompleteProfileStatus.isAffiliationsComplete());
        assertTrue(allCompleteProfileStatus.isAffiliationsComplete());
    }

    @Test
    public void isAgreementComplete() throws Exception {
        assertFalse(nothingCompleteProfileStatus.isAgreementComplete());
        assertFalse(skillsCompleteProfileStatus.isAgreementComplete());
        assertFalse(affiliationsCompleteProfileStatus.isAgreementComplete());
        assertTrue(agreementCompleteProfileStatus.isAgreementComplete());
        assertTrue(allCompleteProfileStatus.isAgreementComplete());
    }

    @Test
    public void isComplete() throws Exception {
        assertFalse(nothingCompleteProfileStatus.isComplete());
        assertFalse(skillsCompleteProfileStatus.isComplete());
        assertFalse(affiliationsCompleteProfileStatus.isComplete());
        assertFalse(agreementCompleteProfileStatus.isComplete());
        assertTrue(allCompleteProfileStatus.isComplete());
    }
}
