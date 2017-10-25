package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.form.domain.FormInput;

import java.util.List;

public interface GuidanceRowTemplatePersistorService extends BaseChainedTemplatePersistorService<List<GuidanceRow>, FormInput> {
    @NotSecured("Has to be secured by calling service.")
    List<GuidanceRow> persistByPrecedingEntity(FormInput precedingEntityType);
    @NotSecured("Has to be secured by calling service.")
    void cleanForPrecedingEntity(FormInput precedingEntityType);
}
