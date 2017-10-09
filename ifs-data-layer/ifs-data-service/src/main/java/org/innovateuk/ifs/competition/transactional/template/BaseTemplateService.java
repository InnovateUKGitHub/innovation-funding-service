package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.commons.service.ServiceResult;

public interface BaseTemplateService<EntityType, RequisiteEntityType> {
    public EntityType createByRequisite(RequisiteEntityType requisiteEntityType);
    public ServiceResult<EntityType> createByTemplate(EntityType templateType);
}
