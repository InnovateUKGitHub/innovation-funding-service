
package org.innovateuk.ifs.application.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_TEAM;

/**
 * This class contains methods to retrieve and store {@link QuestionResource} related data,
 * through the RestService {@link QuestionRestService}.
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    private static final Log LOG = LogFactory.getLog(QuestionServiceImpl.class);
    private static final String ASSIGN_QUESTION_PARAM = "assign_question";

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Override
    public ServiceResult<Void> assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById) {
        return questionStatusRestService.assign(questionId, applicationId, assigneeId, assignedById).toServiceResult();
    }

    @Override
    public List<ValidationMessages> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById) {
        questionStatusRestService.assign(questionId, applicationId, 0L, 0L);
        LOG.debug(String.format("mark question(application details) as complete %s / %s /%s ", questionId, applicationId, markedAsCompleteById));
        return questionStatusRestService.markAsComplete(questionId, applicationId, markedAsCompleteById).getSuccess();
    }

    @Override
    public void markAsIncomplete(Long questionId, Long applicationId, Long markedAsInCompleteById) {
        LOG.debug(String.format("mark section as incomplete %s / %s /%s ", questionId, applicationId, markedAsInCompleteById));
        if (isApplicationTeamQuestion(questionId)) {
            questionStatusRestService.markTeamAsInComplete(questionId, applicationId, markedAsInCompleteById);
        } else {
            questionStatusRestService.markAsInComplete(questionId, applicationId, markedAsInCompleteById);
        }

    }

    private boolean isApplicationTeamQuestion(Long questionId) {
        return getById(questionId).getQuestionSetupType() == APPLICATION_TEAM;
    }

    @Override
    public List<QuestionResource> findByCompetition(Long competitionId) {
        return questionRestService.findByCompetition(competitionId).getSuccess();
    }

    @Override
    public Map<Long, QuestionStatusResource> getQuestionStatusesForApplicationAndOrganisation(Long applicationId, Long userOrganisationId) {
        return mapToQuestionIds(questionStatusRestService.findByApplicationAndOrganisation(applicationId, userOrganisationId).getSuccess());
    }

    private Map<Long, QuestionStatusResource> mapToQuestionIds(final List<QuestionStatusResource> questionStatusResources) {

        final Map<Long, QuestionStatusResource> questionAssignees = new HashMap<>();

        for (QuestionStatusResource questionStatusResource : questionStatusResources) {
            questionAssignees.put(questionStatusResource.getQuestion(), questionStatusResource);
        }

        return questionAssignees;
    }

    @Override
    public List<QuestionStatusResource> getNotificationsForUser(Collection<QuestionStatusResource> questionStatuses, Long userId) {
        return questionStatuses.stream().
                filter(qs -> userId.equals(qs.getAssigneeUserId()) && (qs.getNotified() != null && qs.getNotified().equals(Boolean.FALSE)))
                .collect(Collectors.toList());
    }

    @Override
    public void removeNotifications(List<QuestionStatusResource> questionStatuses) {
        questionStatuses.stream().forEach(qs -> questionStatusRestService.updateNotification(qs.getId(), true));
    }

    @Override
    public Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId) {
        return questionStatusRestService.getMarkedAsComplete(applicationId, organisationId);
    }

    @Override
    public QuestionResource getById(Long questionId) {
        return questionRestService.findById(questionId).getSuccess();
    }

    @Override
    public QuestionResource getByIdAndAssessmentId(Long questionId, Long assessmentId) {
        return questionRestService.getByIdAndAssessmentId(questionId, assessmentId).getSuccess();
    }

    @Override
    public Optional<QuestionResource> getNextQuestion(Long questionId) {
        return questionRestService.getNextQuestion(questionId).getOptionalSuccessObject();
    }

    @Override
    public Optional<QuestionResource> getPreviousQuestion(Long questionId) {
        return questionRestService.getPreviousQuestion(questionId).getOptionalSuccessObject();
    }

    @Override
    public Optional<QuestionResource> getPreviousQuestionBySection(Long sectionId) {
        return questionRestService.getPreviousQuestionBySection(sectionId).getOptionalSuccessObject();
    }

    @Override
    public Optional<QuestionResource> getNextQuestionBySection(Long sectionId) {
        return questionRestService.getNextQuestionBySection(sectionId).getOptionalSuccessObject();
    }

    @Override
    public ServiceResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType) {
        return questionRestService.getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType).toServiceResult();
    }

    @Override
    public QuestionStatusResource getByQuestionIdAndApplicationIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId) {
        List<QuestionStatusResource> questionStatuses = questionStatusRestService.findByQuestionAndApplicationAndOrganisation(questionId, applicationId, organisationId).getSuccess();
        if (questionStatuses == null || questionStatuses.isEmpty()) {
            return null;
        }
        return questionStatuses.get(0);
    }

    @Override
    public Map<Long, QuestionStatusResource> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId) {
        return mapToQuestionIds(questionStatusRestService.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(questionIds, applicationId, organisationId).getSuccess());
    }

    public List<QuestionStatusResource> findQuestionStatusesByQuestionAndApplicationId(Long questionId, Long applicationId) {
        return questionStatusRestService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId).getSuccess();
    }

    @Override
    public List<QuestionResource> getQuestionsBySectionIdAndType(Long sectionId, QuestionType type) {
        return questionRestService.getQuestionsBySectionIdAndType(sectionId, type).getSuccess();
    }

    @Override
    public QuestionResource save(QuestionResource questionResource) {
        return questionRestService.save(questionResource).getSuccess();
    }

    @Override
    public List<QuestionResource> getQuestionsByAssessment(long assessmentId) {
        return questionRestService.getQuestionsByAssessment(assessmentId).getSuccess();
    }

    @Override
    public void assignQuestion(Long applicationId, HttpServletRequest request, ProcessRoleResource assignedBy) {

        Map<String, String[]> params = request.getParameterMap();
        if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
            Long questionId = extractQuestionProcessRoleIdFromAssignSubmit(request);
            Long assigneeId = extractAssigneeProcessRoleIdFromAssignSubmit(request);

            assign(questionId, applicationId, assigneeId, assignedBy.getId()).getSuccess();
        }
    }

    @Override
    public void assignQuestion(Long applicationId, UserResource user, HttpServletRequest request) {
        ProcessRoleResource assignedBy = processRoleService.findProcessRole(user.getId(), applicationId);
        assignQuestion(applicationId, request, assignedBy);
    }

    protected Long extractAssigneeProcessRoleIdFromAssignSubmit(HttpServletRequest request) {
        Long assigneeId = null;
        Map<String, String[]> params = request.getParameterMap();
        if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
            String assign = request.getParameter(ASSIGN_QUESTION_PARAM);
            assigneeId = Long.valueOf(assign.split("_")[1]);
        }

        return assigneeId;
    }

    @Override
    public Long extractQuestionProcessRoleIdFromAssignSubmit(HttpServletRequest request) {
        Long questionId = null;
        Map<String, String[]> params = request.getParameterMap();
        if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
            String assign = request.getParameter(ASSIGN_QUESTION_PARAM);
            questionId = Long.valueOf(assign.split("_")[0]);
        }

        return questionId;
    }
}
