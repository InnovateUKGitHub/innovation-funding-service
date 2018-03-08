package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

/**
 * Base repository to extend to access {@link Invite} based queries.
 *
 * This does not include any queries against the target or owner entities.
 */
@NoRepositoryBean
public interface InviteRepository<T extends Invite> extends PagingAndSortingRepository<T, Long> {

    List<T> getByUserId(long userId);

    List<T> getByIdIn(List<Long> inviteIds);

    T getByHash(String hash);

    List<T> findByEmail(String email);

    Page<T> findByStatus(InviteStatus status, Pageable pageable);

    List<T> findByStatusIn(Set<InviteStatus> status);

    List<T> findByNameLikeAndStatusIn(String name, Set<InviteStatus> status);

    List<T> findByEmailLikeAndStatusIn(String email, Set<InviteStatus> status);
}
