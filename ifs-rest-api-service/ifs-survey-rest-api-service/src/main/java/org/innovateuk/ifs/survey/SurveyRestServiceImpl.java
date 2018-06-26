package org.innovateuk.ifs.survey;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.innovateuk.ifs.commons.exception.ServiceUnavailableException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@Service
public class SurveyRestServiceImpl extends BaseRestService implements SurveyRestService {

    private static final Log LOG = LogFactory.getLog(SurveyRestServiceImpl.class);

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
        LOG.info("Calling Alerts Fallback:", e);
        throw new ServiceUnavailableException();
    }
}
