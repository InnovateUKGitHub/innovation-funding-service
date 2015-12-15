package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rav on 15/12/2015.
 *
 */
@Service
public class QuestionStatusRestServiceImpl extends BaseRestService implements QuestionStatusRestService {
  @Value("${ifs.data.service.rest.questionStatus}")
  String questionStatusRestURL;

  @Override public List<QuestionStatus> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId) {
    return Arrays.asList(restGet(questionStatusRestURL + "/findByQuestionAndAplication/" + questionId + "/" + applicationId, QuestionStatus[].class));
  }
}
