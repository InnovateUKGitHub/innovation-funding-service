package org.innovateuk.ifs.competition.publiccontent.resource;

import org.innovateuk.ifs.util.CompositeId;

/**
 * Class to enable the spring security to apply type information when applying security rules to entity ids.
 * In this case determine that the id in question relates to a content group.
 */
public final class ContentGroupCompositeId extends CompositeId {

    private ContentGroupCompositeId(Long id) {
        super(id);
    }

    public static ContentGroupCompositeId id(Long contentGroupId){
        return new ContentGroupCompositeId(contentGroupId);
    }
}
