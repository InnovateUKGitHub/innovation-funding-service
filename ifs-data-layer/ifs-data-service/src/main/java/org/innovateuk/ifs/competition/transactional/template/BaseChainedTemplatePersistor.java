package org.innovateuk.ifs.competition.transactional.template;

/**
 * Interface for beans that can persist entities by their parent entity.
 */
public interface BaseChainedTemplatePersistor<EntityType, ParentEntityType> {
    EntityType persistByParentEntity(ParentEntityType parentEntityType);
    void cleanForParentEntity(ParentEntityType parentEntityType);
}
