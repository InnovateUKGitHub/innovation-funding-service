package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.cofunder.repository.CofunderAssignmentRepository;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationResource;
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

public class CofunderDashboardServiceImplTest extends BaseServiceUnitTest<CofunderDashboardService> {
    @Mock
    private CofunderAssignmentRepository cofunderAssignmentRepository;

    @Override
    protected CofunderDashboardService supplyServiceUnderTest() {
        return new CofunderDashboardServiceImpl();
    }

    @Test
    public void getApplicationsForCofunding() {
        long userId = 1L;
        long competitionId = 2L;
        PageRequest pageRequest = PageRequest.of(0, 1);
        CofunderDashboardApplicationResource content = new CofunderDashboardApplicationResource();
        Page<CofunderDashboardApplicationResource> page = new PageImpl<>(newArrayList(content), pageRequest, 1L);

        when(cofunderAssignmentRepository.findApplicationsForCofunderCompetitionDashboard(userId, competitionId, pageRequest)).thenReturn(page);

        ServiceResult<CofunderDashboardApplicationPageResource> result = service.getApplicationsForCofunding(userId, competitionId, pageRequest);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSuccess().getContent().get(0), equalTo(content));
        assertThat(result.getSuccess().getTotalElements(), equalTo(1L));
        assertThat(result.getSuccess().getTotalPages(), equalTo(1));
        assertThat(result.getSuccess().getNumber(), equalTo(0));
        assertThat(result.getSuccess().getSize(), equalTo(1));
    }
}