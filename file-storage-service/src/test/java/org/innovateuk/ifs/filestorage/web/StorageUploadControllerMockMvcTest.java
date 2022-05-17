package org.innovateuk.ifs.filestorage.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.innovateuk.ifs.api.filestorage.ApiVersion;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.innovateuk.ifs.filestorage.util.TestHelper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = StorageUploadController.class)
class StorageUploadControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService storageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Disabled
    void upload() throws Exception {
        mockMvc.perform(post("/" + ApiVersion.VERSION_ONE + "/upload")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(TestHelper.build())))
        .andExpect(status().isOk());
    }

    @Test
    void uploadRaw() throws Exception {
        mockMvc.perform(post("/" + ApiVersion.VERSION_ONE + "/uploadRaw")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.TEXT_PLAIN)
                .content("sdsdfsddfs"))
        .andExpect(status().isOk());
    }

    private String toJson(Object obj) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.err.println(json);
        return json;
    }
}
