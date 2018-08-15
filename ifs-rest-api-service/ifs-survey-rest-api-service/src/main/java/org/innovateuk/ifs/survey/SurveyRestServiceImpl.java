package org.innovateuk.ifs.survey;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.innovateuk.ifs.commons.exception.ServiceUnavailableException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
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
        throw new ServiceUnavailableException();
    }
}
