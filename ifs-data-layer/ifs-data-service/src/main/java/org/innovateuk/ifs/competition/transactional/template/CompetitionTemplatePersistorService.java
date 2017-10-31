package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.competition.domain.Competition;

public interface CompetitionTemplatePersistorService extends BaseTemplatePersistorService<Competition> {
    @NotSecured("Has to be secured by calling service.")
    Competition persistByEntity(Competition precedingEntityType);

    @NotSecured("Has to be secured by calling service.")
    void cleanByEntityId(Long competitionId);
}
