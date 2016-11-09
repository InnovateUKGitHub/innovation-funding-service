package com.worth.ifs.assessment.viewmodel.profile;

import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.junit.Assert.*;

public class AssessorProfileStatusViewModelTest {

    private AssessorProfileStatusViewModel nothingCompleteProfileStatus;
    private AssessorProfileStatusViewModel skillsCompleteProfileStatus;
    private AssessorProfileStatusViewModel affiliationsCompleteProfileStatus;
    private AssessorProfileStatusViewModel contractCompleteProfileStatus;
    private AssessorProfileStatusViewModel allCompleteProfileStatus;

    @Before
    public void setUp() throws Exception {
        nothingCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().build());
        skillsCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().withSkillsComplete(true).build());
        affiliationsCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().withAffliliationsComplete(true).build());
        contractCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource().withContractComplete(true).build());
        allCompleteProfileStatus = new AssessorProfileStatusViewModel(newUserProfileStatusResource()
                .withSkillsComplete(true)
                .withAffliliationsComplete(true)
                .withContractComplete(true)
                .build());
    }

    @Test
    public void isSkillsComplete() throws Exception {
        assertFalse(nothingCompleteProfileStatus.isSkillsComplete());
        assertTrue(skillsCompleteProfileStatus.isSkillsComplete());
        assertFalse(affiliationsCompleteProfileStatus.isSkillsComplete());
        assertFalse(contractCompleteProfileStatus.isSkillsComplete());
        assertTrue(allCompleteProfileStatus.isSkillsComplete());
    }

    @Test
    public void isAffiliationsComplete() throws Exception {
        assertFalse(nothingCompleteProfileStatus.isAffiliationsComplete());
        assertFalse(skillsCompleteProfileStatus.isAffiliationsComplete());
        assertTrue(affiliationsCompleteProfileStatus.isAffiliationsComplete());
        assertFalse(contractCompleteProfileStatus.isAffiliationsComplete());
        assertTrue(allCompleteProfileStatus.isAffiliationsComplete());
    }

    @Test
    public void isContractComplete() throws Exception {
        assertFalse(nothingCompleteProfileStatus.isContractComplete());
        assertFalse(skillsCompleteProfileStatus.isContractComplete());
        assertFalse(affiliationsCompleteProfileStatus.isContractComplete());
        assertTrue(contractCompleteProfileStatus.isContractComplete());
        assertTrue(allCompleteProfileStatus.isContractComplete());
    }

    @Test
    public void isComplete() throws Exception {
        assertFalse(nothingCompleteProfileStatus.isComplete());
        assertFalse(skillsCompleteProfileStatus.isComplete());
        assertFalse(affiliationsCompleteProfileStatus.isComplete());
        assertFalse(contractCompleteProfileStatus.isComplete());
        assertTrue(allCompleteProfileStatus.isComplete());
    }
}