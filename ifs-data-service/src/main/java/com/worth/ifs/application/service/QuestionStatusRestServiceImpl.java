package com.worth.ifs.application.service;

import com.google.common.base.Joiner;
import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Service
public class QuestionStatusRestServiceImpl extends BaseRestService implements QuestionStatusRestService {
  @Value("${ifs.data.service.rest.questionStatus}")
  String questionStatusRestURL;

  @Override
  public List<QuestionStatus> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId) {
    return Arrays.asList(restGet(questionStatusRestURL + "/findByQuestionAndApplication/" + questionId + "/" + applicationId, QuestionStatus[].class));
  }

  @Override
  public List<QuestionStatus> findByQuestionAndApplicationAndOrganisation(Long questionId, Long applicationId, Long organisationId) {
    return Arrays.asList(restGet(questionStatusRestURL + "/findByQuestionAndApplicationAndOrganisation/" + questionId + "/" + applicationId + "/" + organisationId, QuestionStatus[].class));
  }

  public List<QuestionStatusResource> findByApplicationAndOrganisation(Long applicationId, Long organisationId) {
    return Arrays.asList(restGet(questionStatusRestURL + "/findByApplicationAndOrganisation/" + applicationId + "/" + organisationId, QuestionStatusResource[].class));
  }

  @Override
  public QuestionStatus findQuestionStatusById(Long id) {
    return restGet(questionStatusRestURL + "/" + id, QuestionStatus.class);
  }

  @Override
  public List<QuestionStatusResource> getByQuestionIdAndApplicationIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId){
    return Arrays.asList(restGet(questionStatusRestURL + "/findByQuestionAndApplicationAndOrganisation/" + questionId + "/" + applicationId + "/" + organisationId, QuestionStatusResource[].class));
  }

  @Override
  public List<QuestionStatusResource> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId) {
    return Arrays.asList(restGet(questionStatusRestURL + "/findByQuestionIdsAndApplicationIdAndOrganisationId/" + Joiner.on(",").join(questionIds) + "/" + applicationId + "/" + organisationId, QuestionStatusResource[].class));
  }

  public List<QuestionStatus> getByIds(List<Long> ids){
    return Arrays.asList(restGet(questionStatusRestURL + "getByIds", QuestionStatus.class));
  }
}
