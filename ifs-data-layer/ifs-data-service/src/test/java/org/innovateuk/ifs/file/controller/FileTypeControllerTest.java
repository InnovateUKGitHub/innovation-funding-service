package org.innovateuk.ifs.file.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.innovateuk.ifs.file.service.FileTypeService;
import org.junit.Test;
import org.mockito.Mock;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FileTypeControllerTest extends BaseControllerMockMVCTest<FileTypeController> {

    @Mock
    private FileTypeService fileTypeServiceMock;

    @Override
    protected FileTypeController supplyControllerUnderTest() {
        return new FileTypeController();
    }

    @Test
    public void findOne() throws Exception {

        long fileTypeId = 1L;

        FileTypeResource fileTypeResource = new FileTypeResource();

        when(fileTypeServiceMock.findOne(fileTypeId)).thenReturn(serviceSuccess(fileTypeResource));

        mockMvc.perform(get("/file/file-type/{fileTypeId}", fileTypeId)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileTypeResource)));

        verify(fileTypeServiceMock, only()).findOne(fileTypeId);
    }

    @Test
    public void findByName() throws Exception {

        String name = "name";

        FileTypeResource fileTypeResource = new FileTypeResource();

        when(fileTypeServiceMock.findByName(name)).thenReturn(serviceSuccess(fileTypeResource));

        mockMvc.perform(get("/file/file-type/findByName/{name}", name)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileTypeResource)));

        verify(fileTypeServiceMock, only()).findByName(name);
    }
}

