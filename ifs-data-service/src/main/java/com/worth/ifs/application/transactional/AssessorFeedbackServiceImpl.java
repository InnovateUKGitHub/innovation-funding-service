package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.repository.AssessorFeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssessorFeedbackServiceImpl implements AssessorFeedbackService {
    @Autowired
    private AssessorFeedbackRepository repository;

    @Override
    public AssessorFeedback findOne(Long id) {
        return repository.findOne(id);
    }
}