package org.innovateuk.ifs.activitylog.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.activitylog.controller.ActivityLogController;
import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.activitylog.resource.ActivityLogResourceBuilder.newActivityLogResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

public class ActivityLogControllerDocumentation extends BaseControllerMockMVCTest<ActivityLogController> {

    @Mock
    private ActivityLogService activityLogService;

    @Override
    protected ActivityLogController supplyControllerUnderTest() {
        return new ActivityLogController();
    }

    @Test
    public void findByApplicationId() throws Exception {
        Long applicationId = 1L;
        List<ActivityLogResource> qq = newActivityLogResource().build(1);
        when(activityLogService.findByApplicationId(applicationId)).thenReturn(serviceSuccess(newActivityLogResource().build(1)));

        mockMvc.perform(get("/activity-log?applicationId={applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }
}