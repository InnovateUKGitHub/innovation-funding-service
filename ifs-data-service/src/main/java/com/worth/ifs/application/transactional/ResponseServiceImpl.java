package com.worth.ifs.application.transactional;


import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

import java.util.ArrayList;
import java.util.List;

import com.worth.ifs.application.mapper.ResponseMapper;
import com.worth.ifs.application.resource.ResponseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;

@Service
public class ResponseServiceImpl extends BaseTransactionalService implements ResponseService {
    @Autowired
    ResponseMapper responseMapper;

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

    @Override
    public ServiceResult<List<ResponseResource>> findResponseResourcesByApplication(final Long applicationId) {
        return findResponsesByApplication(applicationId).andOnSuccessReturn(responses -> responsesToResources(responses));
    }

    private List<ResponseResource> responsesToResources(List<Response> filtered) {
        return simpleMap(filtered, response -> responseMapper.mapToResource(response));
    }
}
