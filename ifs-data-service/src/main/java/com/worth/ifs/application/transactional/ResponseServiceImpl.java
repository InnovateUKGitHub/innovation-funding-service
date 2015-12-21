package com.worth.ifs.application.transactional;


import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResponseServiceImpl implements ResponseService {

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    ResponseRepository responseRepository;

    @Override
    public List<Response> findResponsesByApplication(final Long applicationId) {
        Application app = applicationRepository.findOne(applicationId);
        List<ProcessRole> userAppRoles = app.getProcessRoles();

        List<Response> responses = new ArrayList<>();
        for (ProcessRole userAppRole : userAppRoles) {
            responses.addAll(responseRepository.findByUpdatedBy(userAppRole));
        }
        return responses;
    }
}
