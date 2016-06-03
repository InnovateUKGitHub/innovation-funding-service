package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.resource.QuestionStatusResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.questionStatusResourceListType;
import static org.junit.Assert.assertEquals;

public class QuestionStatusRestServiceMocksTest extends BaseRestServiceUnitTest<QuestionStatusRestServiceImpl> {
    private static final String questionStatusRestURL = "/questionStatus";

    @Override
    protected QuestionStatusRestServiceImpl registerRestServiceUnderTest() {
        QuestionStatusRestServiceImpl questionStatusRestService = new QuestionStatusRestServiceImpl();
        return questionStatusRestService;
    }

    @Test
    public void findQuestionStatusesByQuestionAndApplicationIdTest() {

        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource().build(3);
        setupGetWithRestResultExpectations(questionStatusRestURL + "/findByQuestionAndApplication/1/2", questionStatusResourceListType(), questionStatuses);

        List<QuestionStatusResource> returnedQuestionStatuses = service.findQuestionStatusesByQuestionAndApplicationId(1L, 2L).getSuccessObject();
        assertEquals(questionStatuses, returnedQuestionStatuses);
    }
}
