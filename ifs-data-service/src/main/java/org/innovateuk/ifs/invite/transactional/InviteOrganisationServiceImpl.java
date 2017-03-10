package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.mapper.InviteOrganisationMapper;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class InviteOrganisationServiceImpl extends BaseTransactionalService implements InviteOrganisationService {

    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    private InviteOrganisationMapper mapper;

    @Override
    public ServiceResult<InviteOrganisationResource> findOne(Long id) {
        return find(inviteOrganisationRepository.findOne(id), notFoundError(InviteOrganisation.class, id))
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<InviteOrganisationResource> getByIdWithInvitesForApplication(long id, long applicationId) {
        return find(inviteOrganisationRepository.findOne(id), notFoundError(InviteOrganisation.class, id))
                .andOnSuccessReturn(inviteOrganisation -> mapper.mapToResource(filterApplicationInvites(inviteOrganisation, applicationId)));
    }

    @Override
    public ServiceResult<InviteOrganisationResource> getByOrganisationIdWithInvitesForApplication(long organisationId, long applicationId) {
        return inviteOrganisationRepository.findByInvitesApplicationId(applicationId).stream()
                .filter(inviteOrganisation -> isInviteForOrganisation(inviteOrganisation, organisationId))
                .findFirst()
                .map(inviteOrganisation -> mapper.mapToResource(filterApplicationInvites(inviteOrganisation, applicationId)))
                .map(ServiceResult::serviceSuccess)
                .orElse(serviceFailure(notFoundError(InviteOrganisation.class, asList(organisationId, applicationId))));
    }

    @Override
    public ServiceResult<Iterable<InviteOrganisationResource>> findAll() {
        return find(inviteOrganisationRepository.findAll(), notFoundError(InviteOrganisation.class)).andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<InviteOrganisationResource> save(InviteOrganisationResource inviteOrganisationResource) {
        InviteOrganisation inviteOrganisation = inviteOrganisationRepository.save(mapper.mapToDomain(inviteOrganisationResource));
        return serviceSuccess(mapper.mapToResource(inviteOrganisation));
    }

    private InviteOrganisation filterApplicationInvites(InviteOrganisation inviteOrganisation, long applicationId) {
        List<ApplicationInvite> applicationInvites = inviteOrganisation.getInvites();
        if (applicationInvites != null) {
            inviteOrganisation.setInvites(applicationInvites.stream().filter(applicationInvite ->
                    applicationInvite.getTarget().getId().equals(applicationId)).collect(toList()));
        }
        return inviteOrganisation;
    }

    private boolean isInviteForOrganisation(InviteOrganisation inviteOrganisation, long organisationId) {
        return inviteOrganisation.getOrganisation() != null && inviteOrganisation.getOrganisation().getId().equals(organisationId);
    }
}
