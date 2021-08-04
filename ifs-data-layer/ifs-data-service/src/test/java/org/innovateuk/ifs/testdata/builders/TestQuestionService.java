package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.transactional.QuestionStatusServiceImpl;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * A test service to help speed up webtest generation.
 *
 * Currently one of the slowest operations during webtest data creation is marking questions individually as complete,
 * which is slow because we have to then calculate the overall progress percentage of the Application with each question.
 *
 * In order to facilitate a substantial speedup of this process, we only mark the final question as complete and thus the
 * progress percentage calculation is only invoked once, shaving about 60% of the run-time off the webtest process.
 *
 * This does mean that we have to slightly restructure the production code in and add @Primary to QuestionServiceImpl
 * which isn't ideal, but the true fix for this is addressing the slow code and, once that is in place, we can do
 * away with this service extension
 */
@Service("testQuestionService")
@Lazy
public class TestQuestionService extends QuestionStatusServiceImpl {

    @PreAuthorize("hasPermission(#ids, 'UPDATE')")
    @Transactional
    public ServiceResult<List<ValidationMessages>> markAsCompleteWithoutApplicationCompletionStatusUpdate(final QuestionApplicationCompositeId ids,
                                                                                                          final Long markedAsCompleteById) {
        return setComplete(ids.questionId, ids.applicationId, markedAsCompleteById, true, false);
    }
}
