package org.innovateuk.ifs.threads.repository;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.threads.domain.ProjectStateComments;

import java.util.Optional;

public interface ProjectStateCommentsRepository extends MessageThreadRepository<ProjectStateComments> {

    Optional<ProjectStateComments> findDistinctByClassPkAndClassNameAndClosedDateIsNull(long classPk, String className);

    default Optional<ProjectStateComments> findOpenComments(long projectId) {
        return findDistinctByClassPkAndClassNameAndClosedDateIsNull(projectId, Project.class.getName());
    }
}
