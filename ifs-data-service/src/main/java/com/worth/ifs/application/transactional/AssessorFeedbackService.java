package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.security.NotSecured;

public interface AssessorFeedbackService {
    @NotSecured("TODO")
    AssessorFeedback findOne(Long id);
}