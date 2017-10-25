package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.commons.security.NotSecured;

import java.util.List;

public interface QuestionTemplatePersistorService extends BaseChainedTemplatePersistorService<List<Question>, Section>, BaseTemplatePersistorService<List<Question>> {
    @NotSecured("Has to be secured by calling service.")
    List<Question> persistByPrecedingEntity(Section precedingEntityType);
    @NotSecured("Has to be secured by calling service.")
    void cleanForPrecedingEntity(Section precedingEntityType);
    @NotSecured("Has to be secured by calling service.")
    List<Question> persistByEntity(List<Question> precedingEntityType);
    @NotSecured("Has to be secured by calling service.")
    void deleteEntityById(Long entityId);
}
