package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupCompositeId;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ContentGroupLookupStrategy {

    @PermissionEntityLookupStrategy
    public ContentGroupCompositeId getContentGroupCompositeId(final Long id) {
        return ContentGroupCompositeId.id(id);
    }
}
