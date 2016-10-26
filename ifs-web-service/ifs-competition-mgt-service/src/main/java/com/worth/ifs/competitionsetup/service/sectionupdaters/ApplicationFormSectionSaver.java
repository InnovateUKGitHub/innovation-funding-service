package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.ApplicationFormForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.model.Question;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.competitionsetup.utils.CompetitionUtils.textToBoolean;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;

/**
 * Competition setup section saver for the application form section.
 */
@Service
public class ApplicationFormSectionSaver implements CompetitionSetupSectionSaver {

    @Autowired
    CompetitionSetupQuestionService competitionSetupQuestionService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.APPLICATION_FORM;
	}

	@Override
	public List<Error> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        return Collections.emptyList();
	}

	@Override
	public List<Error> autoSaveSectionField(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> objectId) {
	    if(objectId.isPresent()) {
            Question question = competitionSetupQuestionService.getQuestion(objectId.get());

            if(question == null) {
                return makeErrorList();
            }

            List<Error> errors = updateQuestionWithValueByFieldname(question, fieldName, value);
            if(!errors.isEmpty()) {
                return errors;
            }

            competitionSetupQuestionService.updateQuestion(question);
        } else {
            return makeErrorList();
        }

        return Collections.emptyList();
	}

	private List<Error> updateQuestionWithValueByFieldname(Question question, String fieldName, String value) {
        switch (fieldName) {
            case "question.shortTitle" :
                question.setShortTitle(value);
                break;
            case "question.title" :
                question.setTitle(value);
                break;
            case "question.subTitle" :
                question.setSubTitle(value);
                break;
            case "question.guidanceTitle" :
                question.setGuidanceTitle(value);
                break;
            case "question.guidance" :
                question.setGuidance(value);
                break;
            case "question.maxWords" :
                question.setMaxWords(Integer.parseInt(value));
            case "question.appendix" :
                question.setAppendix(textToBoolean(value));
                break;
            default:
                return makeErrorList();
        }

        return Collections.emptyList();
    }

	private List<Error> makeErrorList() {
        return asList(fieldError("", null, "Unable to save question"));
    }

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return ApplicationFormForm.class.equals(clazz);
	}
}
