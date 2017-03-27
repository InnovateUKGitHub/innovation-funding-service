package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.mapper.InviteOrganisationMapper;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class InviteOrganisationServiceImpl extends BaseTransactionalService implements InviteOrganisationService {

    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    private InviteOrganisationMapper mapper;

    @Override
    public ServiceResult<InviteOrganisationResource> getById(long id) {
        return find(inviteOrganisationRepository.findOne(id), notFoundError(InviteOrganisation.class, id))
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<InviteOrganisationResource> getByOrganisationIdWithInvitesForApplication(long organisationId, long applicationId) {
        return find(inviteOrganisationRepository.findOneByOrganisationIdAndInvitesApplicationId(organisationId, applicationId),
                notFoundError(InviteOrganisation.class, asList(organisationId, applicationId))).andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<InviteOrganisationResource> save(InviteOrganisationResource inviteOrganisationResource) {
        InviteOrganisation inviteOrganisation = inviteOrganisationRepository.save(mapper.mapToDomain(inviteOrganisationResource));
        return serviceSuccess(mapper.mapToResource(inviteOrganisation));
    }
}
