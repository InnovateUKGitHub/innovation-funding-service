package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;

import java.util.List;

/**
 * Created by luke.harper on 25/04/2017.
 */
public class ApplicantQuestionResource extends AbstractApplicantResource {

    private QuestionResource question;

    private List<ApplicantFormInputResource> formInputs;

    private QuestionStatusResource questionStatusResource;


}
