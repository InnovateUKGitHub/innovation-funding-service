package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseResource;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.stringsListType;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class KnowledgeBaseRestServiceImplTest extends BaseRestServiceUnitTest<KnowledgeBaseRestServiceImpl>  {

    protected KnowledgeBaseRestServiceImpl registerRestServiceUnderTest() {
        return new KnowledgeBaseRestServiceImpl();
    }

    private static final String ORGANISATION_BASE_URL = "/organisation/knowledge-base";

    @Test
    public void getKnowledgeBases() {

        String expected = "KnowledgeBase 1";

        setupGetWithRestResultAnonymousExpectations(ORGANISATION_BASE_URL, stringsListType(), Collections.singletonList(expected), OK);
        RestResult<List<String>> result = service.getKnowledgeBases();

        assertEquals(expected, result.getSuccess().get(0));
    }

    @Test
    public void getKnowledgeBaseByName() {

        String expected = "KnowledgeBase 1";

        KnowledgeBaseResource knowledgeBaseResource = new KnowledgeBaseResource();
        knowledgeBaseResource.setName(expected);

        setupGetWithRestResultAnonymousExpectations(ORGANISATION_BASE_URL + "/find-by-name/" + expected, KnowledgeBaseResource.class, knowledgeBaseResource, OK);
        RestResult<KnowledgeBaseResource> result = service.getKnowledgeBaseByName(expected);

        assertEquals(expected, result.getSuccess().getName());
    }

}