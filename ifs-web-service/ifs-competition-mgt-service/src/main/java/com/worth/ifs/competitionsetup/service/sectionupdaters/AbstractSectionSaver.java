package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to hold all the common functionality in the section savers.
 */
public abstract class AbstractSectionSaver {

    @Autowired
    private CompetitionService competitionService;

    protected List<Error> performAutoSaveField(CompetitionResource competitionResource, String fieldName, String value) {
        List<Error> errors = new ArrayList<>();
        try {
            errors = updateCompetitionResourceWithAutoSave(errors, competitionResource, fieldName, value);
        } catch (ParseException|NumberFormatException e) {
            errors.add(new Error("validation.standard.only.numbers", HttpStatus.BAD_REQUEST));
        }

        if(!errors.isEmpty()) {
            return errors;
        }
        competitionService.update(competitionResource);
        return Collections.emptyList();
    }

    protected abstract List<Error> updateCompetitionResourceWithAutoSave(List<Error> errors, CompetitionResource competitionResource, String fieldName, String value) throws ParseException;

}
