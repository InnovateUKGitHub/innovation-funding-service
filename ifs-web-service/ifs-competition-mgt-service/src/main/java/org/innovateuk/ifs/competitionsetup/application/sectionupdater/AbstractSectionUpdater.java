package org.innovateuk.ifs.competitionsetup.application.sectionupdater;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupUpdater;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.apache.commons.beanutils.ConvertUtils.convert;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyType;
import static org.apache.commons.beanutils.PropertyUtils.setNestedProperty;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

/**
 * Class to hold all the common functionality in the section savers.
 */
public abstract class AbstractSectionUpdater implements CompetitionSetupUpdater {

    @Override
    public ServiceResult<Void> autoSaveSectionField(CompetitionResource competitionResource, CompetitionSetupForm form, String fieldName, String value, Optional<Long> questionId) {
        try {
            form.setMarkAsCompleteAction(false);
            Class<?> propertyType = getPropertyType(form, fieldName);
            setNestedProperty(form, fieldName, convert(value, propertyType));
            return saveSection(competitionResource, form);
        } catch (Exception e) {
            return handleIrregularAutosaveCase(competitionResource, fieldName, value, questionId);
        }
    }

    public ServiceResult<Void> saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm) {
        if(!sectionToSave().preventEdit(competitionResource)) {
            return doSaveSection(competitionResource, competitionSetupForm);
        }
        else {
            return serviceFailure(asList(new Error("COMPETITION_NOT_EDITABLE", HttpStatus.BAD_REQUEST)));
        }
    }

    protected abstract ServiceResult<Void> doSaveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm);

    protected ServiceResult<Void> handleIrregularAutosaveCase(CompetitionResource competitionResource,
                                                              String fieldName,
                                                              String value,
                                                              Optional<Long> questionId) {
        return serviceFailure(new Error("Field not found", HttpStatus.BAD_REQUEST));
    }

}
