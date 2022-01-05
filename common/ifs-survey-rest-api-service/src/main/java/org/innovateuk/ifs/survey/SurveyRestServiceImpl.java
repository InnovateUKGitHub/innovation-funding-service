package org.innovateuk.ifs.survey;

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
    public RestResult<Void> save(SurveyResource surveyResource) {
        try {
            return postWithRestResultAnonymous("/survey", surveyResource, Void.class);
        } catch (Throwable t) {
            return RestResult.restFailure(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
