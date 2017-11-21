package org.innovateuk.ifs.competition.publiccontent.resource;

import org.innovateuk.ifs.util.CompositeId;

/**
 * TODO
 */
public final class ContentGroupCompositeId extends CompositeId {

    private ContentGroupCompositeId(Long id) {
        super(id);
    }

    public static ContentGroupCompositeId id(Long contentGroupId){
        return new ContentGroupCompositeId(contentGroupId);
    }
}
