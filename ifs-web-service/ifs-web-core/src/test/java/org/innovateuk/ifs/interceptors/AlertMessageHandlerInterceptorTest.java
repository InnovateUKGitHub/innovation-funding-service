package org.innovateuk.ifs.interceptors;

import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.alert.service.AlertRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AlertMessageHandlerInterceptorTest {

    @InjectMocks
    private AlertMessageHandlerInterceptor interceptor;

    @Mock
    private AlertRestService alertRestService;

    @Test
    public void alertsPresent() {
        //given
        AlertResource alert = new AlertResource();
        when(alertRestService.findAllVisible()).thenReturn(restSuccess(asList(alert)));

        ModelAndView mav = new ModelAndView();

        //when
        interceptor.postHandle(null, null, null, mav);

        //then
        assertTrue(mav.getModelMap().containsKey("alertMessages"));
        assertEquals(1, ((List<AlertResource>)mav.getModelMap().get("alertMessages")).size());
        assertEquals(alert, ((List<AlertResource>)mav.getModelMap().get("alertMessages")).get(0));
    }

    @Test
    public void alertsNotPresent() {
        //given
        when(alertRestService.findAllVisible()).thenReturn(restSuccess(asList()));

        ModelAndView mav = new ModelAndView();

        //when
        interceptor.postHandle(null, null, null, mav);

        //then
        assertFalse(mav.getModelMap().containsKey("alertMessages"));
    }

    @Test
    public void alertsServiceNotSuccessful() {
        //given
        when(alertRestService.findAllVisible()).thenReturn(restFailure(new Error("some.key", HttpStatus.NOT_FOUND)));

        ModelAndView mav = new ModelAndView();

        //when
        interceptor.postHandle(null, null, null, mav);

        //then
        assertFalse(mav.getModelMap().containsKey("alertMessages"));
    }
}
