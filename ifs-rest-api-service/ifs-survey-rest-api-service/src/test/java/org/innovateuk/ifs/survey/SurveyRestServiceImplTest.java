package org.innovateuk.ifs.survey;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.RootAnonymousUserRestTemplateAdaptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.survey.builder.SurveyResourceBuilder.newSurveyResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SurveyRestServiceImplTest  {

    @InjectMocks
    private SurveyRestServiceImpl surveyRestService;

    @Mock
    private RootAnonymousUserRestTemplateAdaptor rootAnonymousUserRestTemplateAdaptor;

    @Test
    public void save() {
        String baseUrl = "base";
        surveyRestService.setServiceUrl(baseUrl);

        SurveyResource surveyResource = newSurveyResource().build();
        RestResult<Void> expected = mock(RestResult.class);

        when(rootAnonymousUserRestTemplateAdaptor.postWithRestResult(baseUrl + "/survey", surveyResource, Void.class)).thenReturn(expected);

        RestResult<Void> result = surveyRestService.save(surveyResource);

        assertEquals(result, expected);
    }

}