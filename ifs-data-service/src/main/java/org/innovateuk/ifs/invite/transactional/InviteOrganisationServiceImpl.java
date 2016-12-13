package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.mapper.InviteOrganisationMapper;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class InviteOrganisationServiceImpl extends BaseTransactionalService implements InviteOrganisationService {

    @Autowired
    private InviteOrganisationRepository repository;

    @Autowired
    private InviteOrganisationMapper mapper;

    @Override
     public ServiceResult<InviteOrganisationResource> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(InviteOrganisation.class, id)).andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<Iterable<InviteOrganisationResource>> findAll() {
        return find(repository.findAll(), notFoundError(InviteOrganisation.class)).andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<InviteOrganisationResource> save(InviteOrganisationResource inviteOrganisationResource) {
        InviteOrganisation inviteOrganisation = repository.save(mapper.mapToDomain(inviteOrganisationResource));
        return ServiceResult.serviceSuccess(mapper.mapToResource(inviteOrganisation));
    }
}
