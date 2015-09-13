package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

public interface ResponseService {
    public List<Response> getByApplication(Long applicationId);
    public void assignQuestion(Long applicationId, Long questionId, Long userId, Long assigneeId);
    public HashMap<Long, Response> mapResponsesToQuestion(List<Response> responses);
    public void markQuestionAsComplete(Long applicationId, Long questionId, Long userId);
    public void markQuestionAsInComplete(Long applicationId, Long questionId, Long userId);
    public Boolean save(Long userId, Long applicationId, Long questionId, String value);
}
