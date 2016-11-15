package com.worth.ifs.application.transactional;

import com.worth.ifs.application.mapper.QuestionAssessmentMapper;
import com.worth.ifs.application.repository.QuestionAssessmentRepository;
import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service focused around the processing of the assesment of questions
 */
@Service
public class QuestionAssessmentServiceImpl extends BaseTransactionalService implements QuestionAssessmentService {
    private static final Log LOG = LogFactory.getLog(QuestionAssessmentServiceImpl.class);

    @Autowired
    private QuestionAssessmentRepository questionAssessmentRepository;

    @Autowired
    private QuestionAssessmentMapper questionAssessmentMapper;

    @Override
    public ServiceResult<QuestionAssessmentResource> getById(Long id) {
        return find(questionAssessmentRepository.findOne(id), notFoundError(QuestionAssessmentResource.class, id))
                .andOnSuccessReturn(questionAssessmentMapper::mapToResource);
    }

    @Override
    public ServiceResult<QuestionAssessmentResource> findByQuestion(Long questionId) {
        return find(questionAssessmentRepository.findByQuestionId(questionId), notFoundError(QuestionAssessmentResource.class, questionId))
                .andOnSuccessReturn(questionAssessmentMapper::mapToResource);
    }

}