package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    QuestionRestService questionRestService;


    @Override
    public void assign(Long questionId, Long assigneeId) {
        questionRestService.assign(questionId, assigneeId);
    }

    @Override
    public void markAsComplete(Long questionId, Long markedAsCompleteById) {
        questionRestService.markAsComplete(questionId, markedAsCompleteById);
    }

    @Override
    public void markAsInComplete(Long questionId, Long markedAsInCompleteById) {
        questionRestService.markAsInComplete(questionId, markedAsInCompleteById);
    }

    @Override
    public List<Question> findByCompetition(Long competitionId) {
        return questionRestService.findByCompetition(competitionId);
    }

    @Override
    public HashMap<Long, QuestionStatus> mapAssigneeToQuestion(List<Question> questions) {
        HashMap<Long, QuestionStatus> questionAssignees = new HashMap<>();
        for(Question question : questions) {
            for(QuestionStatus questionStatus : question.getQuestionStatuses()) {
                if(questionStatus.getAssignee()!=null) {
                    questionAssignees.put(question.getId(), questionStatus);
                    break;
                }
            }
        }
        return questionAssignees;
    }
}
