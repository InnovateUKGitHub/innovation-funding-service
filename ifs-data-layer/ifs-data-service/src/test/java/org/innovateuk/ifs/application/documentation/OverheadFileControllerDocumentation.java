package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.OverheadFileController;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.finance.transactional.OverheadFileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.controller.OverheadFileControllerTest.OVERHEAD_BASE_URL;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.FileEntryDocs.fileAndContentsFields;
import static org.innovateuk.ifs.documentation.FileEntryDocs.fileEntryResourceFields;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileControllerUtils.class})
public class OverheadFileControllerDocumentation extends BaseControllerMockMVCTest<OverheadFileController> {
    @Mock
    private OverheadFileService overheadFileServiceMock;

    @Override
    protected OverheadFileController supplyControllerUnderTest() {
        return new OverheadFileController();
    }

    @Test
    public void getProjectFileDetailsTest() throws Exception {
        Long overHeadIdSuccess = 123L;

        FileEntryResource fileEntryResource = newFileEntryResource().withId(overHeadIdSuccess).build();

        when(overheadFileServiceMock.getProjectFileEntryDetails(overHeadIdSuccess)).thenReturn(serviceSuccess(fileEntryResource));

        mockMvc.perform(get(OVERHEAD_BASE_URL + "/projectOverheadCalculationDocumentDetails?overheadId={overHeadIdSuccess}", overHeadIdSuccess))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(fileEntryResource)))
                .andDo(document("overheadcalculation/{method-name}",
                        requestParameters(
                                parameterWithName("overheadId").description("Id of overhead cost in project finances")
                        ),
                        responseFields(fileEntryResourceFields)
        ));
    }

    @Test
    public void getProjectFileContentsTest() throws Exception {
        Long overHeadIdSuccess = 123L;

        FileEntryResource fileEntryResource = newFileEntryResource().withId(overHeadIdSuccess).build();
        FileAndContents successResult = new BasicFileAndContents(fileEntryResource, () -> mock(InputStream.class));
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity(successResult, HttpStatus.OK);

        mockStatic(FileControllerUtils.class);
        when(FileControllerUtils.handleFileDownload(any(Supplier.class))).thenReturn(objectResponseEntity);

        when(overheadFileServiceMock.getProjectFileEntryContents(overHeadIdSuccess)).thenReturn(serviceSuccess(successResult));

        mockMvc.perform(get(OVERHEAD_BASE_URL + "/projectOverheadCalculationDocument?overheadId={overHeadIdSuccess}", overHeadIdSuccess))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(successResult)))
                .andDo(document("overheadcalculation/{method-name}",
                        requestParameters(
                                parameterWithName("overheadId").description("Id of overhead cost in project finances")
                        ),
                        responseFields(fileAndContentsFields)));
    }
}