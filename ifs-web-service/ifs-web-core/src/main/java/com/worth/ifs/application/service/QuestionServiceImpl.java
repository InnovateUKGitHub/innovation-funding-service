package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.resource.QuestionType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link QuestionResource} related data,
 * through the RestService {@link QuestionRestService}.
 */
// TODO DW - INFUND-1555 - handle rest results
@Service
public class QuestionServiceImpl implements QuestionService {
    private static final Log LOG = LogFactory.getLog(SectionServiceImpl.class);
    private static final String ASSIGN_QUESTION_PARAM = "assign_question";

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Override
    public void assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById) {
        questionRestService.assign(questionId, applicationId, assigneeId, assignedById);
    }

    @Override
    public List<ValidationMessages> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById) {
        questionRestService.assign(questionId, applicationId, 0L, 0L);
        LOG.debug(String.format("mark question(application details) as complete %s / %s /%s ", questionId, applicationId, markedAsCompleteById));
        return questionRestService.markAsComplete(questionId, applicationId, markedAsCompleteById).getSuccessObject();
    }

    @Override
    public void markAsInComplete(Long questionId, Long applicationId, Long markedAsInCompleteById) {
        LOG.debug(String.format("mark section as incomplete %s / %s /%s ", questionId, applicationId, markedAsInCompleteById));
        questionRestService.markAsInComplete(questionId, applicationId, markedAsInCompleteById);
    }

    @Override
    public List<QuestionResource> findByCompetition(Long competitionId) {
        return questionRestService.findByCompetition(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public Map<Long, QuestionStatusResource> getQuestionStatusesForApplicationAndOrganisation(Long applicationId, Long userOrganisationId) {
        return mapToQuestionIds(questionStatusRestService.findByApplicationAndOrganisation(applicationId, userOrganisationId).getSuccessObjectOrThrowException());
    }

    private Map<Long, QuestionStatusResource> mapToQuestionIds(final List<QuestionStatusResource> questionStatusResources){

        final Map<Long, QuestionStatusResource> questionAssignees = new HashMap<>();

        for(QuestionStatusResource questionStatusResource : questionStatusResources){
            questionAssignees.put(questionStatusResource.getQuestion(), questionStatusResource);
        }

        return questionAssignees;
    }

    @Override
    public List<QuestionStatusResource> getNotificationsForUser(Collection<QuestionStatusResource> questionStatuses, Long userId) {
        return questionStatuses.stream().
                filter(qs ->  userId.equals(qs.getAssigneeUserId()) && (qs.getNotified() != null && qs.getNotified().equals(Boolean.FALSE)))
                .collect(Collectors.toList());
    }

    @Override
    public void removeNotifications(List<QuestionStatusResource> questionStatuses) {
        questionStatuses.stream().forEach(qs -> questionRestService.updateNotification(qs.getId(), true));
    }

    @Override
    public Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId) {
        return questionRestService.getMarkedAsComplete(applicationId, organisationId);
    }

    @Override
    public QuestionResource getById(Long questionId) {
        return questionRestService.findById(questionId).getSuccessObjectOrThrowException();
    }

    @Override
    public QuestionResource getByIdAndAssessmentId(Long questionId, Long assessmentId) {
        return questionRestService.getByIdAndAssessmentId(questionId, assessmentId).getSuccessObjectOrThrowException();
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
    public RestResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(Long competitionId, String formInputType) {
        return questionRestService.getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType);
    }

    @Override
    public QuestionStatusResource getByQuestionIdAndApplicationIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId){
        List<QuestionStatusResource> questionStatuses = questionStatusRestService.findByQuestionAndApplicationAndOrganisation(questionId, applicationId, organisationId).getSuccessObjectOrThrowException();
        if(questionStatuses == null || questionStatuses.isEmpty()){
            return null;
        }
        return questionStatuses.get(0);
    }

    @Override
    public Map<Long, QuestionStatusResource> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId){
        return mapToQuestionIds(questionStatusRestService.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(questionIds, applicationId, organisationId).getSuccessObjectOrThrowException());
    }

    public List<QuestionStatusResource> findQuestionStatusesByQuestionAndApplicationId(Long questionId, Long applicationId) {
        return questionStatusRestService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId).getSuccessObjectOrThrowException();
    }

	@Override
	public List<QuestionResource> getQuestionsBySectionIdAndType(Long sectionId, QuestionType type) {
		return questionRestService.getQuestionsBySectionIdAndType(sectionId, type).getSuccessObjectOrThrowException();
	}

    @Override
    public QuestionResource save(QuestionResource questionResource) {
        return questionRestService.save(questionResource).getSuccessObjectOrThrowException();
    }

    @Override
    public List<QuestionResource> getQuestionsByAssessment(Long assessmentId) {
        return questionRestService.getQuestionsByAssessment(assessmentId).getSuccessObjectOrThrowException();
    }

    @Override
    public void assignQuestion(Long applicationId, HttpServletRequest request, ProcessRoleResource assignedBy) {

        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey(ASSIGN_QUESTION_PARAM)){
            Long questionId = extractQuestionProcessRoleIdFromAssignSubmit(request);
            Long assigneeId = extractAssigneeProcessRoleIdFromAssignSubmit(request);

            assign(questionId, applicationId, assigneeId, assignedBy.getId());
        }
    }

    protected Long extractAssigneeProcessRoleIdFromAssignSubmit(HttpServletRequest request) {
        Long assigneeId = null;
        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey(ASSIGN_QUESTION_PARAM)){
            String assign = request.getParameter(ASSIGN_QUESTION_PARAM);
            assigneeId = Long.valueOf(assign.split("_")[1]);
        }

        return assigneeId;
    }

    @Override
    public Long extractQuestionProcessRoleIdFromAssignSubmit(HttpServletRequest request) {
        Long questionId = null;
        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey(ASSIGN_QUESTION_PARAM)){
            String assign = request.getParameter(ASSIGN_QUESTION_PARAM);
            questionId = Long.valueOf(assign.split("_")[0]);
        }

        return questionId;
    }

}
