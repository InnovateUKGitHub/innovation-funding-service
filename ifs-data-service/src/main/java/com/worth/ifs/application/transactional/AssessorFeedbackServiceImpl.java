package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.mapper.AssessorFeedbackMapper;
import com.worth.ifs.application.repository.AssessorFeedbackRepository;
import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class AssessorFeedbackServiceImpl extends BaseTransactionalService implements AssessorFeedbackService {

    @Autowired
    private AssessorFeedbackRepository repository;

    @Autowired
    private AssessorFeedbackMapper mapper;

    @Override
    public ServiceResult<AssessorFeedbackResource> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(AssessorFeedback.class, id)).andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<AssessorFeedbackResource> findByAssessorId(Long assessorId) {
        return find(repository.findByAssessorId(assessorId), notFoundError(AssessorFeedback.class, assessorId)).andOnSuccessReturn(mapper::mapToResource);
    }
}