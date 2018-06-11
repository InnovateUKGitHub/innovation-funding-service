package org.innovateuk.ifs.survey;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.RootDefaultRestTemplateAdaptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.survey.builder.SurveyResourceBuilder.newSurveyResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.class)
public class SurveyRestServiceImplTest  {

    @InjectMocks
    private SurveyRestServiceImpl surveyRestService;

    @Mock
    private RootDefaultRestTemplateAdaptor rootDefaultRestTemplateAdaptor;

    @Test
    public void save() {
        String baseUrl = "base";
        setField(surveyRestService, "baseUrl", baseUrl);

        SurveyResource surveyResource = newSurveyResource().build();
        RestResult<Void> expected = mock(RestResult.class);

        when(rootDefaultRestTemplateAdaptor.postWithRestResult(baseUrl + "/survey", surveyResource, Void.class)).thenReturn(expected);

        RestResult<Void> result = surveyRestService.save(surveyResource);

        assertEquals(result, expected);
    }

}