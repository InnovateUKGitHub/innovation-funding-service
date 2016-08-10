package com.worth.ifs.form.service;

import com.worth.ifs.form.resource.FormInputResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.form.resource.FormInputScope.APPLICATION;
import static com.worth.ifs.form.resource.FormInputScope.ASSESSMENT;

/**
 * This class contains methods to retrieve and store {@link FormInputResource} related data,
 * through the RestService {@link FormInputRestService}.
 */
@Service
public class FormInputServiceImpl implements FormInputService {

    @Autowired
    private FormInputRestService formInputRestService;

    @Override
    public FormInputResource getOne(Long formInputId) {
        return formInputRestService.getById(formInputId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<FormInputResource> findApplicationInputsByQuestion(Long questionId) {
        return formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION).getSuccessObjectOrThrowException();
    }

    @Override
    public List<FormInputResource> findAssessmentInputsByQuestion(Long questionId) {
        return formInputRestService.getByQuestionIdAndScope(questionId, ASSESSMENT).getSuccessObjectOrThrowException();
    }

    @Override
    public List<FormInputResource> findApplicationInputsByCompetition(Long competitionId) {
        return formInputRestService.getByCompetitionIdAndScope(competitionId, APPLICATION).getSuccessObjectOrThrowException();
    }

    @Override
    public List<FormInputResource> findAssessmentInputsByCompetition(Long competitionId) {
        return formInputRestService.getByCompetitionIdAndScope(competitionId, ASSESSMENT).getSuccessObjectOrThrowException();
    }

    @Override
    public void delete(Long formInputId) {
        formInputRestService.delete(formInputId).getSuccessObjectOrThrowException();
    }

    @Override
    public FormInputResource save(FormInputResource formInputResource) {
        return formInputRestService.save(formInputResource).getSuccessObjectOrThrowException();
    }
}
