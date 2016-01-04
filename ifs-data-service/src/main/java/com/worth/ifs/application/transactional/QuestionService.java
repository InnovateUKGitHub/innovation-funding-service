package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.security.NotSecured;

import java.util.List;
import java.util.Set;

/**
 * Transactional and secure service for Question processing work
 */
public interface QuestionService {

    @NotSecured("TODO")
    Question getQuestionById(final Long id);

    @NotSecured("TODO")
    void markAsComplete(final Long questionId,
                        final Long applicationId,
                        final Long markedAsCompleteById);


    @NotSecured("TODO")
    void markAsInComplete(final Long questionId,
                          final Long applicationId,
                          final Long markedAsInCompleteById);

    @NotSecured("TODO")
    void assign(final Long questionId,
                final Long applicationId,
                final Long assigneeId,
                final Long assignedById);

    @NotSecured("TODO")
    Set<Long> getMarkedAsComplete(Long applicationId,
                                  Long organisationId);

    @NotSecured("TODO")
    void updateNotification(final Long questionStatusId,
                            final Boolean notify);

    @NotSecured("TODO")
    List<Question> findByCompetition(final Long competitionId);

    @NotSecured("TODO")
    Question getNextQuestion(final Long questionId);

    @NotSecured("TODO")
    Question getPreviousQuestionBySection(final Long sectionId);

    @NotSecured("TODO")
    Question getNextQuestionBySection(final Long sectionId);

    @NotSecured("TODO")
    Question getPreviousQuestion(final Long questionId);

    @NotSecured("TODO")
    Boolean isMarkedAsComplete(Question question, Long applicationId, Long organisationId);
}
