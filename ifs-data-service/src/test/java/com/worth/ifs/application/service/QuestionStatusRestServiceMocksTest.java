package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.domain.QuestionStatus;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.application.builder.QuestionStatusBuilder.newQuestionStatus;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.questionStatusListType;
import static org.junit.Assert.assertEquals;

public class QuestionStatusRestServiceMocksTest extends BaseRestServiceUnitTest<QuestionStatusRestServiceImpl> {
    private static final String questionStatusRestURL = "/questionStatus";

    @Override
    protected QuestionStatusRestServiceImpl registerRestServiceUnderTest() {
        QuestionStatusRestServiceImpl questionStatusRestService = new QuestionStatusRestServiceImpl();
        questionStatusRestService.questionStatusRestURL = questionStatusRestURL;
        return questionStatusRestService;
    }

    @Test
    public void findQuestionStatusesByQuestionAndApplicationIdTest() {

        List<QuestionStatus> questionStatuses = newQuestionStatus().build(3);
        setupGetWithRestResultExpectations(questionStatusRestURL + "/findByQuestionAndApplication/1/2", questionStatusListType(), questionStatuses);

        List<QuestionStatus> returnedQuestionStatuses = service.findQuestionStatusesByQuestionAndApplicationId(1L, 2L).getSuccessObject();
        assertEquals(questionStatuses, returnedQuestionStatuses);
    }
}
