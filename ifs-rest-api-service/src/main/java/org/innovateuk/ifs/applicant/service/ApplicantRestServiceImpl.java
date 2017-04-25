package org.innovateuk.ifs.applicant.service;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * Created by luke.harper on 25/04/2017.
 */
@Service
public class ApplicantRestServiceImpl extends BaseRestService implements ApplicantRestService {
    private String applicantBaseUrl = "/applicant";

    @Override
    public ApplicantQuestionResource getQuestion(Long user, Long application, Long question) {
        return getWithRestResult(format("%s/%d/%d/question/%d", applicantBaseUrl, user, application, question), ApplicantQuestionResource.class).getSuccessObjectOrThrowException();
    }

    @Override
    public ApplicantSectionResource getSection(Long user, Long application, Long section) {
        return getWithRestResult(format("%s/%d/%d/section/%d", applicantBaseUrl, user, application, section), ApplicantSectionResource.class).getSuccessObjectOrThrowException();
    }
}
