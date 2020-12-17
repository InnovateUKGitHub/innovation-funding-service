package org.innovateuk.ifs.procurement.milestone.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.procurement.milestone.controller.AbstractProcurementMilestoneControllerTest.ControllerClazz;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.transactional.ProcurementMilestoneService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AbstractProcurementMilestoneControllerTest extends BaseControllerMockMVCTest<ControllerClazz> {

    @Mock
    private ServiceClazz service;

    @Test
    public void create() throws Exception {
        ResourceClazz resource = new ResourceClazz();
        resource.setDeliverable("Deliverables");

        when(service.create(refEq(resource))).thenReturn(serviceSuccess(resource));

        mockMvc.perform(MockMvcRequestBuilders.post("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deliverable", is("Deliverables")));

        verify(service).create(refEq(resource));
    }

    @Test
    public void get() throws Exception {
        long id = 1L;
        ResourceClazz resource = new ResourceClazz();
        resource.setDeliverable("Deliverables");

        when(service.get(refEq(IdClazz.of(id)))).thenReturn(serviceSuccess(resource));

        mockMvc.perform(MockMvcRequestBuilders.get("/test/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliverable", is("Deliverables")));

        verify(service).get(refEq(IdClazz.of(id)));
    }

    @Test
    public void update() throws Exception {
        long id = 1L;
        ResourceClazz resource = new ResourceClazz();
        resource.setDeliverable("Deliverables");

        when(service.update(refEq(resource))).thenReturn(serviceSuccess(resource));

        mockMvc.perform(MockMvcRequestBuilders.put("/test/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk());

        verify(service).update(refEq(resource));
    }

    @Test
    public void delete() throws Exception {
        long id = 1L;

        when(service.delete(refEq(IdClazz.of(id)))).thenReturn(serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.delete("/test/{id}", id))
                .andExpect(status().isNoContent());

        verify(service).delete(refEq(IdClazz.of(id)));
    }

    @Override
    protected ControllerClazz supplyControllerUnderTest() {
        return new ControllerClazz();
    }

    private static class ResourceClazz extends ProcurementMilestoneResource {}

    private abstract class ServiceClazz implements ProcurementMilestoneService<ResourceClazz, IdClazz> {};

    private static class IdClazz extends ProcurementMilestoneId {
        public static IdClazz of(long id) {
            IdClazz idClazz = new IdClazz();
            idClazz.setId(id);
            return idClazz;
        }
    };

    @RestController
    @RequestMapping("/test")
    public class ControllerClazz extends AbstractProcurementMilestoneController<ResourceClazz, IdClazz> {

        @Override
        protected ProcurementMilestoneService<ResourceClazz, IdClazz> getProcurementMilestoneService() {
            return service;
        }

        @Override
        protected IdClazz getId(long id) {
            return IdClazz.of(id);
        }
    }
}