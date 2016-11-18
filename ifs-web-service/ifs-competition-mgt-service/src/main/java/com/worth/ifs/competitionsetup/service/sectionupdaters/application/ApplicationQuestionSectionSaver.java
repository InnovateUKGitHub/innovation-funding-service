package com.worth.ifs.competitionsetup.service.sectionupdaters.application;

import com.worth.ifs.application.service.*;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.*;
import com.worth.ifs.competitionsetup.service.*;
import com.worth.ifs.competitionsetup.service.sectionupdaters.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.error.Error.*;
import static com.worth.ifs.competitionsetup.utils.CompetitionUtils.*;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;

/**
 * Competition setup section saver for the application form section.
 */
@Service
public class ApplicationQuestionSectionSaver implements CompetitionSetupSubsectionSaver {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

	@Override
	public CompetitionSetupSubsection sectionToSave() {
		return CompetitionSetupSubsection.QUESTIONS;
	}

	@Override
	public List<Error> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        return Collections.emptyList();
	}

	@Override
	public List<Error> autoSaveSectionField(CompetitionResource competitionResource, String fieldName,
                                            String value, Optional<Long> objectId) {
	    if(objectId.isPresent()) {
            CompetitionSetupQuestionResource question = competitionSetupQuestionService.getQuestion(objectId.get()).getSuccessObjectOrThrowException();

            if(question == null) {
                return makeErrorList();
            }

            List<Error> errors = updateQuestionWithValueByFieldname(question, fieldName, value);
            if(!errors.isEmpty()) {
                return errors;
            }

            competitionSetupQuestionService.updateQuestion(question);
        } else {
            return makeErrorList("question.maxWords");
        }

        return Collections.emptyList();
	}

	private List<Error> updateQuestionWithValueByFieldname(CompetitionSetupQuestionResource question, String fieldName, String value) {
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
                try {
                    question.setMaxWords(Integer.parseInt(value));
                } catch(NumberFormatException e) {
                    return makeErrorList("question.maxWords");
                }
                break;
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

    private List<Error> makeErrorList(String fieldName) {
        return asList(fieldError("", fieldName, "Unable to save question"));
    }

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return ApplicationFormForm.class.equals(clazz);
	}
}
