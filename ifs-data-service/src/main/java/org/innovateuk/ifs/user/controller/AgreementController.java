package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.innovateuk.ifs.user.transactional.AgreementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This RestController exposes CRUD operations for
 * {@link org.innovateuk.ifs.user.transactional.AgreementServiceImpl} to manage {@link Agreement} related data.
 */

@RestController
@RequestMapping("/agreement")
public class AgreementController {
    @Autowired
    private AgreementService agreementService;

    @GetMapping("/findCurrent")
    public RestResult<AgreementResource> findCurrent() {
        return agreementService.getCurrent().toGetResponse();
    }
}
