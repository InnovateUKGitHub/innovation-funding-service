package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.mapper.InviteOrganisationMapper;
import com.worth.ifs.invite.repository.InviteOrganisationRepository;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

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