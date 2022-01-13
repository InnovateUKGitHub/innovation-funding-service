package org.innovateuk.ifs.project.resource;

import org.innovateuk.ifs.util.CompositeId;

/**
 * Class to enable the spring security to apply type information when applying security rules to entity ids.
 * In this case determine that the id in question relates to a project.
 */
public final class ProjectCompositeId extends CompositeId {

    private ProjectCompositeId(Long id) {
        super(id);
    }

    public static ProjectCompositeId id(Long projectId){
        return new ProjectCompositeId(projectId);
    }
}
