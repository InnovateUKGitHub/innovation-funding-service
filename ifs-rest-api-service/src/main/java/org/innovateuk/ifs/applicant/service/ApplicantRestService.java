package org.innovateuk.ifs.applicant.service;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;

/**
 * Created by luke.harper on 25/04/2017.
 */
public interface ApplicantRestService {

    ApplicantQuestionResource getQuestion(Long user, Long application, Long question);
    ApplicantSectionResource getSection(Long user, Long application, Long section);
}
