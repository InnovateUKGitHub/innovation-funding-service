package org.innovateuk.ifs.setup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.innovateuk.ifs.setup.transactional.SetupStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questionSetupStatus")
public class SetupStatusController {

    @Autowired
    private SetupStatusService setupStatusService;

    @GetMapping("/findByTarget/{targetClassName}/{targetId}")
    public RestResult<List<SetupStatusResource>> findByTarget(@PathVariable("targetClassName") String targetClassName,
                                                              @PathVariable("targetId") Long targetId) {
        return setupStatusService.findByTargetClassNameAndTargetId(targetClassName, targetId).toGetResponse();
    }

    @GetMapping("/findByTargetAndParent/{targetClassName}/{targetId}/{parentId}")
    public RestResult<List<SetupStatusResource>> findByTargetAndParent(@PathVariable("targetClassName") String targetClassName,
                                                                       @PathVariable("targetId") Long targetId,
                                                                       @PathVariable("parentId") Long parentId) {
        return setupStatusService.findByTargetClassNameAndTargetIdAndParentId(targetClassName, targetId, parentId).toGetResponse();
    }

    @GetMapping("/findByClassAndParent/{className}/{parentId}")
    public RestResult<List<SetupStatusResource>> findByClassAndParent(@PathVariable("className") String className,
                                                                      @PathVariable("parentId") Long parentId) {
        return setupStatusService.findByClassNameAndParentId(className, parentId).toGetResponse();
    }

    @GetMapping("/findSetupStatus/{className}/{classPk}")
    public RestResult<SetupStatusResource> findSetupStatus(@PathVariable("className") String className,
                                                           @PathVariable("classPk") Long classPk) {
        return setupStatusService.findSetupStatus(className, classPk).toGetResponse();
    }

    @PostMapping("/save")
    public RestResult<SetupStatusResource> saveSetupStatus(@RequestBody SetupStatusResource setupStatusResource) {
        return setupStatusService.saveSetupStatus(setupStatusResource).toPostWithBodyResponse();
    }
}
