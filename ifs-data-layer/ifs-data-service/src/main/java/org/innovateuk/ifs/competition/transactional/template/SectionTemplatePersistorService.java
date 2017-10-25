package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.competition.domain.Competition;

import java.util.List;

public interface SectionTemplatePersistorService extends BaseChainedTemplatePersistorService<List<Section>, Competition> {
    @NotSecured("Has to be secured by calling service.")
    List<Section> persistByPrecedingEntity(Competition precedingEntityType);
    @NotSecured("Has to be secured by calling service.")
    void cleanForPrecedingEntity(Competition precedingEntityType);
}
