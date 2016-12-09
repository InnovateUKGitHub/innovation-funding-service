package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;

import java.util.Optional;

/**
 * The interface for saving and autosaving competition forms.
 */
public interface CompetitionSetupSaver {

	ServiceResult<Void> autoSaveSectionField(CompetitionResource competitionResource, CompetitionSetupForm form, String fieldName, String value, Optional<Long> ObjectId);

	boolean supportsForm(Class<? extends CompetitionSetupForm> clazz);

	ServiceResult<Void>  saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm);

}
