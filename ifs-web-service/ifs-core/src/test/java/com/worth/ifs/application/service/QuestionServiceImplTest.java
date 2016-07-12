package com.worth.ifs.application.service;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;


import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionType;

public class QuestionServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private QuestionService service = new QuestionServiceImpl();
    @Mock
    private QuestionRestService questionRestService;

    @Test
    public void testGetQuestionsByType() {
    	QuestionResource section = newQuestionResource().build();
    	when(questionRestService.getQuestionsBySectionIdAndType(1L, QuestionType.COST)).thenReturn(restSuccess(asList(section)));
    	
    	List<QuestionResource> result = service.getQuestionsBySectionIdAndType(1L, QuestionType.COST);
    	
    	assertEquals(1, result.size());
    	assertEquals(section, result.get(0));
    }
}
