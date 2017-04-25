package org.innovateuk.ifs.applicant.transactional;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by luke.harper on 25/04/2017.
 */
public class ApplicantServiceImpl extends BaseTransactionalService implements ApplicantService {

    @Autowired
    private QuestionService questionService;

    @Override
    public ApplicantQuestionResource getQuestion(Long userId, Long questionId, Long applicationId) {
        ApplicantQuestionResource applicant = new ApplicantQuestionResource();

        return null;
    }

    @Override
    public ApplicantSectionResource getSection(Long userId, Long sectionId, Long applicationId) {
        return null;
    }
}
