package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.*;

public class QuestionStatusRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<QuestionStatusRepository> {

    private long questionId;
    private long applicationId;


    @Autowired
    private QuestionStatusRepository repository;

    @Override
    @Autowired
    protected void setRepository(QuestionStatusRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        questionId = 13L;
        applicationId = 1L;
    }

    @Test
    public void findByQuestionIdAndApplicationIdAndAssigneeIdAndOrganisationId() {
        List<QuestionStatus> questionStatuses = repository.findByQuestionIdAndApplicationId(questionId, applicationId);
        assertEquals(1, questionStatuses.size());
    }

    @Test
    public void findByApplicationIdAndAssigneeOrganisationId() {
        List<QuestionStatus> questionStatuses = repository.findByApplicationId(applicationId);
        assertEquals(34, questionStatuses.size());
    }

    @Test
    public void findByApplicationIdAndMarkedAsCompleteByIdOrAssigneeIdOrAssignedById() throws Exception {
        List<Question> questions = newQuestion()
                .withId(102L, 104L, 106L)
                .build(3);
        Application application = newApplication()
                .withId(applicationId)
                .build();
        ProcessRole otherProcessRole = newProcessRole()
                .withId(1L)
                .build();
        ProcessRole targetProcessRole = newProcessRole()
                .withId(28L)
                .build();

        QuestionStatus completedQuestionStatus = new QuestionStatus(questions.get(0), application, targetProcessRole, true);
        completedQuestionStatus.setAssignee(otherProcessRole, otherProcessRole, ZonedDateTime.now());

        List<QuestionStatus> questionStatusesToSave = asList(
                new QuestionStatus(questions.get(1), application, targetProcessRole, otherProcessRole, ZonedDateTime.now()),
                new QuestionStatus(questions.get(2), application, otherProcessRole, targetProcessRole, ZonedDateTime.now()),
                completedQuestionStatus
        );

        repository.save(questionStatusesToSave);
        flushAndClearSession();

        List<QuestionStatus> questionStatusesFound = repository.findByApplicationIdAndMarkedAsCompleteByIdOrAssigneeIdOrAssignedById(
                application.getId(),
                targetProcessRole.getId(),
                targetProcessRole.getId(),
                targetProcessRole.getId()
        );

        assertEquals(3, questionStatusesFound.size());

        Predicate<QuestionStatus> matcher = questionStatus -> {
            Set<Long> processRoleIds = newHashSet(
                    ofNullable(questionStatus.getAssignedBy()).map(ProcessRole::getId).orElse(null),
                    ofNullable(questionStatus.getAssignee()).map(ProcessRole::getId).orElse(null),
                    ofNullable(questionStatus.getMarkedAsCompleteBy()).map(ProcessRole::getId).orElse(null)
            );

            return processRoleIds.contains(targetProcessRole.getId());
        };

        assertThat(questionStatusesFound.get(0), lambdaMatches(matcher));
        assertThat(questionStatusesFound.get(1), lambdaMatches(matcher));
        assertThat(questionStatusesFound.get(2), lambdaMatches(matcher));
    }
}
