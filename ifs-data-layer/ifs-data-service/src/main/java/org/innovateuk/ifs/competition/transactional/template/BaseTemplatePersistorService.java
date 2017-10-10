package org.innovateuk.ifs.competition.transactional.template;

public interface BaseTemplatePersistorService<EntityType> {
    EntityType persistByEntity(EntityType precedingEntityType);
}
