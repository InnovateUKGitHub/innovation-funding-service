package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.InviteHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InviteHistoryRepository extends PagingAndSortingRepository<InviteHistory, Long> {


    void deleteInviteHistoryByInvite(Invite invite);
}
