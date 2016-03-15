package com.worth.ifs.application.transactional;


import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class ResponseServiceImpl extends BaseTransactionalService implements ResponseService {

    @Autowired
    private ResponseRepository responseRepository;

    @Override
    public ServiceResult<List<Response>> findResponsesByApplication(final Long applicationId) {

        return find(application(applicationId)).andOnSuccess(application -> {

            List<ProcessRole> userAppRoles = application.getProcessRoles();

            List<Response> responses = new ArrayList<>();
            for (ProcessRole userAppRole : userAppRoles) {
                responses.addAll(responseRepository.findByUpdatedBy(userAppRole));
            }
            return serviceSuccess(responses);
        });
    }
}
