package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ApplicationInviteRepository extends JpaRepository<ApplicationInvite, Long> {

    List<ApplicationInvite> findByNameLikeAndStatusIn(String name, Set<InviteStatus> status);
    List<ApplicationInvite> findByEmailLikeAndStatusIn(String email, Set<InviteStatus> status);
    List<ApplicationInvite> findByInviteOrganisationOrganisationNameLikeAndStatusIn(String organisationName, Set<InviteStatus> status);
    List<ApplicationInvite> findByApplicationId(@Param("applicationId") Long applicationId);
    ApplicationInvite getByHash(@Param("hash") String hash);
}
