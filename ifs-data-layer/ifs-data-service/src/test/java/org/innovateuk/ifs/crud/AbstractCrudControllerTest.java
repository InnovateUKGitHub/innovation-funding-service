package org.innovateuk.ifs.crud;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.crud.AbstractCrudControllerTest.TestCrudController;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AbstractCrudControllerTest extends BaseControllerMockMVCTest<TestCrudController> {

    @Mock
    private TestService service;

    @Test
    public void create() throws Exception {
        TestResource resource = new TestResource();
        resource.setValue("Value");

        when(service.create(refEq(resource))).thenReturn(serviceSuccess(resource));

        mockMvc.perform(MockMvcRequestBuilders.post("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value", is("Value")));

        verify(service).create(refEq(resource));
    }

    @Test
    public void get() throws Exception {
        long id = 1L;
        TestResource resource = new TestResource();
        resource.setValue("Value");

        when(service.get(id)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(MockMvcRequestBuilders.get("/test/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is("Value")));

        verify(service).get(id);
    }

    @Test
    public void update() throws Exception {
        long id = 1L;
        TestResource resource = new TestResource();
        resource.setValue("Value");

        when(service.update(eq(id), refEq(resource))).thenReturn(serviceSuccess(resource));

        mockMvc.perform(MockMvcRequestBuilders.put("/test/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk());

        verify(service).update(eq(id), refEq(resource));
    }

    @Test
    public void delete() throws Exception {
        long id = 1L;

        when(service.delete(id)).thenReturn(serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.delete("/test/{id}", id))
                .andExpect(status().isNoContent());

        verify(service).delete(id);
    }

    @Test
    public void testGet() throws Exception {
        List<Long> ids = newArrayList(1L, 2L);
        TestResource resource1 = new TestResource();
        resource1.setValue("Value1");
        TestResource resource2 = new TestResource();
        resource2.setValue("Value2");

        when(service.get(ids)).thenReturn(serviceSuccess(newArrayList(resource1, resource2)));

        mockMvc.perform(MockMvcRequestBuilders.get("/test?ids=1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value", is("Value1")))
                .andExpect(jsonPath("$[1].value", is("Value2")));

        verify(service).get(ids);
    }

    @Override
    protected TestCrudController supplyControllerUnderTest() {
        return new TestCrudController();
    }

    private static class TestResource {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    private interface TestService extends IfsCrudService<TestResource, Long> {}


    @RestController
    @RequestMapping("/test")
    public class TestCrudController extends AbstractCrudController<TestResource, Long> {

        @Override
        protected IfsCrudService<TestResource, Long> crudService() {
            return service;
        }
    }
}