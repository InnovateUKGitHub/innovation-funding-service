package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.form.domain.FormInput;

import java.util.List;

public interface FormInputTemplatePersistorService extends BaseChainedTemplatePersistorService<List<FormInput>, Question> {
    @NotSecured("Has to be secured by calling service.")
    List<FormInput> persistByPrecedingEntity(Question precedingEntityType);
    @NotSecured("Has to be secured by calling service.")
    void cleanForPrecedingEntity(Question precedingEntityType);
}
