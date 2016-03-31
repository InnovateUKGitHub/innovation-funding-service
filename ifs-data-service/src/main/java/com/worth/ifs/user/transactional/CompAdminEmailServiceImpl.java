package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.CompAdminEmail;
import com.worth.ifs.user.repository.CompAdminEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class CompAdminEmailServiceImpl extends BaseTransactionalService implements CompAdminEmailService {

    @Autowired
    private CompAdminEmailRepository repository;

    @Override
    public ServiceResult<CompAdminEmail> getByEmail(String email) {
        return find(repository.findOneByEmail(email), notFoundError(CompAdminEmail.class, email));
    }
}
