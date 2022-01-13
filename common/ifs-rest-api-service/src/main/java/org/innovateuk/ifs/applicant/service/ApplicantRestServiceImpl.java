package org.innovateuk.ifs.applicant.service;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * Rest service implementation for retrieving rich applicant resources.
 */
@Service
public class ApplicantRestServiceImpl extends BaseRestService implements ApplicantRestService {

    private static final String BASE_URL = "/applicant";
    private static final String USER_APPLICATIONS_DASHBOARD = "%s/%d/applications/dashboard";

    @Override
    public ApplicantQuestionResource getQuestion(Long user, Long application, Long question) {
        return getWithRestResult(format("%s/%d/%d/question/%d", BASE_URL, user, application, question), ApplicantQuestionResource.class).getSuccess();
    }

    @Override
    public ApplicantSectionResource getSection(Long user, Long application, Long section) {
        return getWithRestResult(format("%s/%d/%d/section/%d", BASE_URL, user, application, section), ApplicantSectionResource.class).getSuccess();
    }

    @Override
    public ApplicantDashboardResource getApplicantDashboard(long user) {
        String path = format(USER_APPLICATIONS_DASHBOARD, BASE_URL, user);
        return getWithRestResult(path, ApplicantDashboardResource.class).getSuccess();
    }

}
