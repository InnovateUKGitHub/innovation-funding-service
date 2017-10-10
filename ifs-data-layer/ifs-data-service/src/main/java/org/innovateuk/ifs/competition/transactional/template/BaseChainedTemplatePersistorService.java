package org.innovateuk.ifs.competition.transactional.template;

public interface BaseChainedTemplatePersistorService<EntityType, PrecedingEntityType> {
    EntityType persistByPrecedingEntity(PrecedingEntityType precedingEntityType);
    void cleanForPrecedingEntity(PrecedingEntityType precedingEntityType);
}
