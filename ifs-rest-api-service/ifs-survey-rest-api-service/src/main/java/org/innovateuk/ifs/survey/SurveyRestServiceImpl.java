package org.innovateuk.ifs.survey;

import com.netflix.hystrix.exception.HystrixTimeoutException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public class SurveyRestServiceImpl extends BaseRestService implements SurveyRestService {

    @Value("${ifs.survey.data.service.baseURL}")
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    @HystrixCommand(fallbackMethod = "saveFallback")
    public RestResult<Void> save(SurveyResource surveyResource) {
        return postWithRestResultAnonymous( "/survey", surveyResource, Void.class);
    }

    public RestResult<Void> saveFallback(SurveyResource surveyResource, Throwable e) {

       if(e.getClass().equals(HystrixTimeoutException.class)) {
            return RestResult.restSuccess();
        }

        return RestResult.restFailure(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
