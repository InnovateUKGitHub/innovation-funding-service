package org.innovateuk.ifs.applicant.transactional;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

/**
 * Created by luke.harper on 25/04/2017.
 */
public interface ApplicantService {

    ServiceResult<ApplicantQuestionResource> getQuestion(Long userId, Long questionId, Long applicationId);
    ServiceResult<ApplicantSectionResource> getSection(Long userId, Long sectionId, Long applicationId);
}
