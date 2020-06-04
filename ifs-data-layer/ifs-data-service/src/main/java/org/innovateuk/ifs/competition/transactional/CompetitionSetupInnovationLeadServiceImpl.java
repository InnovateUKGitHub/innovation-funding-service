package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CompetitionSetupInnovationLeadServiceImpl extends BaseTransactionalService implements CompetitionSetupInnovationLeadService {

    @Autowired
    private InnovationLeadRepository innovationLeadRepository;

    @Autowired
    private UserMapper userMapper;

    private ServiceResult<Competition> findCompetitionById(long id) {
        return find(competitionRepository.findById(id), notFoundError(Competition.class, id));
    }

    @Override
    public ServiceResult<List<UserResource>> findInnovationLeads(long competitionId) {
        List<User> innovationLeads = innovationLeadRepository.findAvailableInnovationLeadsNotAssignedToCompetition(competitionId);
        List<UserResource> innovationLeadUsers = simpleMap(innovationLeads, user -> userMapper
                .mapToResource(user));

        return serviceSuccess(innovationLeadUsers);
    }

    @Override
    public ServiceResult<List<UserResource>> findAddedInnovationLeads(long competitionId) {
        List<User> innovationLeads = innovationLeadRepository.findInnovationsLeadsAssignedToCompetition(competitionId);
        List<UserResource> innovationLeadUsers = simpleMap(innovationLeads, user -> userMapper
                .mapToResource(user));

        return serviceSuccess(innovationLeadUsers);
    }

    @Override
    @Transactional
    public ServiceResult<Void> addInnovationLead(long competitionId, long innovationLeadUserId) {

        return findCompetitionById(competitionId)
                .andOnSuccessReturnVoid(competition ->
                        find(userRepository.findById(innovationLeadUserId),
                                notFoundError(User.class, innovationLeadUserId))
                                .andOnSuccess(innovationLead -> {
                                    innovationLeadRepository.save(new InnovationLead(competition, innovationLead));
                                    return serviceSuccess();
                                })
                );
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeInnovationLead(long competitionId, long innovationLeadUserId) {
        return find(innovationLeadRepository.findInnovationLead(competitionId, innovationLeadUserId),
                notFoundError(InnovationLead.class, competitionId, innovationLeadUserId))
                .andOnSuccessReturnVoid(innovationLead -> innovationLeadRepository.delete(innovationLead));
    }
}
