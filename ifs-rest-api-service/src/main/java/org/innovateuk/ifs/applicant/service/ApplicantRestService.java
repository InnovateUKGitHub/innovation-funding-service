package org.innovateuk.ifs.applicant.service;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;

/**
 * Rest service interface for retrieving rich applicant resources.
 */
public interface ApplicantRestService {

    ApplicantQuestionResource getQuestion(Long user, Long application, Long question);
    ApplicantSectionResource getSection(Long user, Long application, Long section);
    ApplicantDashboardResource getApplicantDashboard(long user);
}
