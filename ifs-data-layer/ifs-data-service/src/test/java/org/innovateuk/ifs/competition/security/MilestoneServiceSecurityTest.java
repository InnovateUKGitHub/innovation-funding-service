package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MilestoneServiceSecurityTest extends BaseServiceSecurityTest<MilestoneService> {

    private MilestonePermissionRules rules;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(MilestonePermissionRules.class);
        initMocks(this);
    }

    @Override
    protected Class<? extends MilestoneService> getClassUnderTest() {
        return TestMilestoneService.class;
    }

    @Test
    public void getCompetitionById() {
        setLoggedInUser(null);

        assertAccessDenied(() -> classUnderTest.getAllMilestonesByCompetitionId(1L), () -> {
            verify(rules).allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(anyLong(), isNull(UserResource.class));
            verify(rules).innovationLeadsCanViewMilestonesOnAssginedComps(anyLong(), isNull(UserResource.class));
            verifyNoMoreInteractions(rules);
        });
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestMilestoneService implements MilestoneService {

        @Override
        public ServiceResult<List<MilestoneResource>> getAllPublicMilestonesByCompetitionId(Long id) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> allPublicDatesComplete(Long id) {
            return null;
        }

        @Override
        public ServiceResult<List<MilestoneResource>> getAllMilestonesByCompetitionId(Long id) {
            return null;
        }

        @Override
        public ServiceResult<MilestoneResource> getMilestoneByTypeAndCompetitionId(MilestoneType type, Long id) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateMilestones(List<MilestoneResource> milestones) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateMilestone(MilestoneResource milestone) {
            return null;
        }

        @Override
        public ServiceResult<MilestoneResource> create(MilestoneType type, Long id) {
            return null;
        }
    }
}
