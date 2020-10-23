package org.innovateuk.ifs.supporter.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationPageResource;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class SupporterDashboardServiceImplTest extends BaseServiceUnitTest<SupporterDashboardService> {
    @Mock
    private SupporterAssignmentRepository supporterAssignmentRepository;

    @Override
    protected SupporterDashboardService supplyServiceUnderTest() {
        return new SupporterDashboardServiceImpl();
    }

    @Test
    public void getApplicationsForCofunding() {
        long userId = 1L;
        long competitionId = 2L;
        PageRequest pageRequest = PageRequest.of(0, 1);
        SupporterDashboardApplicationResource content = new SupporterDashboardApplicationResource();
        Page<SupporterDashboardApplicationResource> page = new PageImpl<>(newArrayList(content), pageRequest, 1L);

        when(supporterAssignmentRepository.findApplicationsForSupporterCompetitionDashboard(userId, competitionId, pageRequest)).thenReturn(page);

        ServiceResult<SupporterDashboardApplicationPageResource> result = service.getApplicationsForCofunding(userId, competitionId, pageRequest);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSuccess().getContent().get(0), equalTo(content));
        assertThat(result.getSuccess().getTotalElements(), equalTo(1L));
        assertThat(result.getSuccess().getTotalPages(), equalTo(1));
        assertThat(result.getSuccess().getNumber(), equalTo(0));
        assertThat(result.getSuccess().getSize(), equalTo(1));
    }
}