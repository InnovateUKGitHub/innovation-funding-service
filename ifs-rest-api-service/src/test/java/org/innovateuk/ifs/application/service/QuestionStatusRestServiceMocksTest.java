package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.questionStatusResourceListType;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
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

        List<QuestionStatusResource> questionStatuses = Arrays.asList(1,2,3).stream().map(i -> new QuestionStatusResource()).collect(Collectors.toList());// newQuestionStatusResource().build(3);
        setupGetWithRestResultExpectations(questionStatusRestURL + "/findByQuestionAndApplication/1/2", questionStatusResourceListType(), questionStatuses);

        List<QuestionStatusResource> returnedQuestionStatuses = service.findQuestionStatusesByQuestionAndApplicationId(1L, 2L).getSuccess();
        assertEquals(questionStatuses, returnedQuestionStatuses);
    }


    @Test
    public void findByQuestionAndApplicationAndOrganisationTest() {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long organisationId = 3L;

        List<QuestionStatusResource> questionStatuses = Arrays.asList(1,2,3).stream().map(i -> new QuestionStatusResource()).collect(Collectors.toList());;
        setupGetWithRestResultExpectations(questionStatusRestURL + "/findByQuestionAndApplicationAndOrganisation/" + questionId + "/" + applicationId + "/" + organisationId, questionStatusResourceListType(), questionStatuses);

        List<QuestionStatusResource> returnedQuestionStatuses = service.findByQuestionAndApplicationAndOrganisation(questionId, applicationId, organisationId).getSuccess();
        assertEquals(questionStatuses, returnedQuestionStatuses);
    }

    @Test
    public void findByApplicationAndOrganisationTest() {
        Long applicationId = 2L;
        Long organisationId = 3L;

        List<QuestionStatusResource> questionStatuses = Arrays.asList(1,2,3).stream().map(i -> new QuestionStatusResource()).collect(Collectors.toList());
        setupGetWithRestResultExpectations(questionStatusRestURL + "/findByApplicationAndOrganisation/" + applicationId + "/" + organisationId, questionStatusResourceListType(), questionStatuses);

        List<QuestionStatusResource> returnedQuestionStatuses = service.findByApplicationAndOrganisation(applicationId, organisationId).getSuccess();
        assertEquals(questionStatuses, returnedQuestionStatuses);
    }

    @Test
    public void findQuestionStatusByIdTest() {
        Long id = 2L;

        QuestionStatusResource questionStatus = new QuestionStatusResource();
        setupGetWithRestResultExpectations(questionStatusRestURL + "/" + id, QuestionStatusResource.class, questionStatus);

        QuestionStatusResource returnedQuestionStatus = service.findQuestionStatusById(id).getSuccess();
        Assert.assertEquals(questionStatus, returnedQuestionStatus);
    }

    @Test
    public void getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationIdTest() {
        List<Long> questionIds = asList(1L, 2L);
        Long applicationId = 2L;
        Long organisationId = 3L;

        List<QuestionStatusResource> questionStatuses = Arrays.asList(1,2,3).stream().map(i -> new QuestionStatusResource()).collect(Collectors.toList());
        setupGetWithRestResultExpectations(questionStatusRestURL + "/findByQuestionIdsAndApplicationIdAndOrganisationId/" + simpleJoiner(questionIds, ",") + "/" + applicationId + "/" + organisationId, questionStatusResourceListType(), questionStatuses);

        List<QuestionStatusResource> returnedQuestionStatuses = service.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(questionIds, applicationId, organisationId).getSuccess();
        assertEquals(questionStatuses, returnedQuestionStatuses);
    }

}
