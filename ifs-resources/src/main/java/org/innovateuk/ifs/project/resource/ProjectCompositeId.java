package org.innovateuk.ifs.project.resource;

import org.innovateuk.ifs.util.CompositeId;

/**
 * TODO
 */
public final class ProjectCompositeId extends CompositeId {

    private ProjectCompositeId(Long id) {
        super(id);
    }

    public static ProjectCompositeId id(Long projectId){
        return new ProjectCompositeId(projectId);
    }
}
