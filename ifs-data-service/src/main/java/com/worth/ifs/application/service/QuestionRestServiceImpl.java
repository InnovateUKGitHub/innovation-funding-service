package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.commons.service.BaseRestServiceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class QuestionRestServiceImpl extends BaseRestServiceProvider implements  QuestionRestService {
    @Value("${ifs.data.service.rest.question}")
    String questionRestURL;

    @Override
    public void markAsComplete(Long questionId, Long markedAsCompleteById) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(dataRestServiceURL + questionRestURL + "/markAsComplete/"+questionId + "/" + markedAsCompleteById, questionId, markedAsCompleteById);
    }

    @Override
    public void markAsInComplete(Long questionId, Long markedAsInCompleteById) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(dataRestServiceURL + questionRestURL + "/markAsInComplete/"+questionId + "/" + markedAsInCompleteById, questionId, markedAsInCompleteById);
    }

    @Override
    public void assign(Long questionId, Long assigneeId, Long assignedById) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(dataRestServiceURL + questionRestURL + "/assign/" + questionId + "/" + assigneeId + "/" + assignedById, questionId, assigneeId, assignedById);
    }

    @Override
    public List<Question> findByCompetition(Long competitionId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Question[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + questionRestURL + "/findByCompetition/" + competitionId, Question[].class);
        Question[] applications = responseEntity.getBody();
        return Arrays.asList(applications);
    }

    @Override
    public void updateNotification(Long questionStatusId, Boolean notify) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(dataRestServiceURL + questionRestURL + "/updateNotification/" + questionStatusId + "/" + notify , questionStatusId, notify);
    }
}
