package org.innovateuk.ifs.activitylog.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.activitylog.controller.ActivityLogController;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.activitylog.resource.ActivityLogResourceBuilder.newActivityLogResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

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
        when(activityLogService.findByApplicationId(applicationId)).thenReturn(serviceSuccess(newActivityLogResource().build(1)));

        mockMvc.perform(get("/activity-log?applicationId={applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("project/{method-name}",
                        requestParameters(
                                parameterWithName("applicationId").description("Id of the application to find activities")
                        ),
                        responseFields(
                                fieldWithPath("[].activityType").description("The type of activity"),
                                fieldWithPath("[].authoredBy").description("The id of the user who authored the activity"),
                                fieldWithPath("[].authoredByName").description("The name of the user who authored the activity"),
                                fieldWithPath("[].authoredByRoles").description("The roles of the user who authored the activity"),
                                fieldWithPath("[].createdOn").description("The date the activity was created"),
                                fieldWithPath("[].organisation").description("The id of the organisation who was the target of the activity.").optional(),
                                fieldWithPath("[].organisationName").description("The name of the organisation who was the target of the activity.").optional(),
                                fieldWithPath("[].documentConfig").description("The id of document involved in the activity").optional(),
                                fieldWithPath("[].documentConfigName").description("The name of document involved in the activity").optional(),
                                fieldWithPath("[].query").description("The id of query involved in the activity").optional(),
                                fieldWithPath("[].queryType").description("The type of query involved in the activity").optional()
                        )
                ));
    }
}