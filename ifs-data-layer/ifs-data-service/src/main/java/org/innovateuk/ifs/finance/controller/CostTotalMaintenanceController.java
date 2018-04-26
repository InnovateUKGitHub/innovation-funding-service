package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.totals.service.AllFinanceTotalsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cost")
public class CostTotalMaintenanceController {

    @Autowired
    private AllFinanceTotalsSender allFinanceTotalsSender;

    @PutMapping("/sendAll")
    public RestResult<Void> sendAll() {
        return allFinanceTotalsSender.sendAllFinanceTotals().toPutResponse();
    }
}
