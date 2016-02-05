package com.worth.ifs.invite.transactional;

import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.repository.InviteOrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InviteOrganisationServiceImpl implements InviteOrganisationService {
    @Autowired
    private InviteOrganisationRepository repository;

    @Override
    public InviteOrganisation findOne(Long id) {
        return repository.findOne(id);
    }
}