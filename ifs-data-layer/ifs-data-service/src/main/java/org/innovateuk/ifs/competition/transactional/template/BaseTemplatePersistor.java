package org.innovateuk.ifs.competition.transactional.template;

/**
 * Interface for beans that can persist entities and potentially subsequent entities.
 */
public interface BaseTemplatePersistor<EntityType> {
    EntityType persistByEntity(EntityType precedingEntityType);
}
