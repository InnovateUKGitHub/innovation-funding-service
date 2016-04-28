package com.worth.ifs.form.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.form.resource.FormInputResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link Response} related data.
 */
public interface FormInputService {
    FormInputResource getOne(Long formInputId);
    List<FormInputResource> findByQuestion(Long questionId);
    List<FormInputResource> findByCompetitionId(Long competitionId);
}
