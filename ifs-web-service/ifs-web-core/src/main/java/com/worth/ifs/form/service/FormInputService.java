package com.worth.ifs.form.service;

import com.worth.ifs.form.resource.FormInputResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link FormInputResource} related data.
 */
public interface FormInputService {
    FormInputResource getOne(Long formInputId);

    List<FormInputResource> findApplicationInputsByQuestion(Long questionId);

    List<FormInputResource> findAssessmentInputsByQuestion(Long questionId);

    List<FormInputResource> findApplicationInputsByCompetition(Long competitionId);

    List<FormInputResource> findAssessmentInputsByCompetition(Long competitionId);

    void delete(Long id);

    FormInputResource save(FormInputResource formInputResource);
}
