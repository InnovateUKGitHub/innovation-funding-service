package org.innovateuk.ifs.applicant.transactional;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;

/**
 * Created by luke.harper on 25/04/2017.
 */
public interface ApplicantService {

    ApplicantQuestionResource getQuestion(Long userId, Long questionId, Long applicationId);
    ApplicantSectionResource getSection(Long userId, Long sectionId, Long applicationId);
}
