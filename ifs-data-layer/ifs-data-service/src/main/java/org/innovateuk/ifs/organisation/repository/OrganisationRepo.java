package org.innovateuk.ifs.organisation.repository;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.Organisation_;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.domain.ProjectUser_;
import org.innovateuk.ifs.project.core.domain.Project_;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.domain.User_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

@Repository
public class OrganisationRepo {

    @Autowired
    private EntityManager entityManager;

    public Organisation findByUserAndProjectId(Long userId, Long projectId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Organisation> cq = cb.createQuery(Organisation.class);

//        "SELECT o FROM Organisation o " +
//        "JOIN ProjectUser pu ON o.id = pu.organisation.id "
        Root<Organisation> organisationRoot = cq.from(Organisation.class);
        Join<Organisation, ProjectUser> projectUserJoin = organisationRoot.join(Organisation_.projectUsers);

        //WHERE pu.user.id = :userId
        Join<ProjectUser, User> user = projectUserJoin.join(ProjectUser_.user);
        user.on(cb.equal(user.get(User_.id), userId));

        //"AND pu.project.id = :projectId"
        Join<ProjectUser, Project> project = projectUserJoin.join(ProjectUser_.project);
        project.on(cb.equal(project.get(Project_.id), projectId));

        return entityManager.createQuery(cq).getSingleResult();

    }

}
