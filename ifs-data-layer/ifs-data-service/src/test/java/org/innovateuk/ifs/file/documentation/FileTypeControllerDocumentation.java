package org.innovateuk.ifs.file.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.file.builder.FileTypeResourceBuilder;
import org.innovateuk.ifs.file.controller.FileTypeController;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.innovateuk.ifs.file.service.FileTypeService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FileTypeControllerDocumentation extends BaseControllerMockMVCTest<FileTypeController> {

    @Mock
    private FileTypeService fileTypeServiceMock;

    @Override
    protected FileTypeController supplyControllerUnderTest() {
        return new FileTypeController();
    }

    @Test
    public void findOne() throws Exception {

        long fileTypeId = 1L;

        FileTypeResource fileTypeResource = FileTypeResourceBuilder.newFileTypeResource()
                .withName("Spreadsheet")
                .withExtension("xls, xlsx")
                .build();

        when(fileTypeServiceMock.findOne(fileTypeId)).thenReturn(serviceSuccess(fileTypeResource));

        mockMvc.perform(get("/file/file-type/{fileTypeId}", fileTypeId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileTypeResource)));

        verify(fileTypeServiceMock, only()).findOne(fileTypeId);
    }

    @Test
    public void findByName() throws Exception {

        String name = "Spreadsheet";

        FileTypeResource fileTypeResource = FileTypeResourceBuilder.newFileTypeResource()
                .withName("Spreadsheet")
                .withExtension("xls, xlsx")
                .build();

        when(fileTypeServiceMock.findByName(name)).thenReturn(serviceSuccess(fileTypeResource));

        mockMvc.perform(get("/file/file-type/find-by-name/{name}", name)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileTypeResource)));

        verify(fileTypeServiceMock, only()).findByName(name);
    }
}


