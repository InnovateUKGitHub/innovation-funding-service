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

    @Override
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

        /*
         * Hystrix command will time out on first request due to the time it
         * takes to initialise.
         *
         * A solution to this will be covered under IFS-4163
         */
       if(e.getClass().equals(HystrixTimeoutException.class)) {
            return RestResult.restSuccess();
        }

        return RestResult.restFailure(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
