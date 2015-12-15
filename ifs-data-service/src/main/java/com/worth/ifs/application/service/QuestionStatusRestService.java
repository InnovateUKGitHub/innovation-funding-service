package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;

import java.util.List;

/**
 * Created by rav on 15/12/2015.
 *
 */
public interface QuestionStatusRestService {
  List<QuestionStatus> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId);
}
