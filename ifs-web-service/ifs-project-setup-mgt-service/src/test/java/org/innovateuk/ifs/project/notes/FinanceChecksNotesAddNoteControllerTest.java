package org.innovateuk.ifs.project.notes;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.notes.controller.FinanceChecksNotesAddNoteController;
import org.innovateuk.ifs.project.notes.form.FinanceChecksNotesAddNoteForm;
import org.innovateuk.ifs.project.notes.viewmodel.FinanceChecksNotesAddNoteViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.util.JsonUtil;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.NoteResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.util.*;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceChecksNotesAddNoteControllerTest extends BaseControllerMockMVCTest<FinanceChecksNotesAddNoteController> {

    private Long projectId = 3L;
    private Long applicantOrganisationId = 22L;
    private Long projectFinanceId = 45L;

    ApplicationResource applicationResource = newApplicationResource().build();

    ProjectResource projectResource = newProjectResource().withId(projectId).withName("Project1").withApplication(applicationResource).build();

    OrganisationResource leadOrganisationResource = newOrganisationResource().withName("Org1").withId(applicantOrganisationId).build();

    ProjectUserResource projectUser = newProjectUserResource().withOrganisation(applicantOrganisationId).withUserName("User1").withEmail("e@mail.com").withPhoneNumber("0117").withRoleName(UserRoleType.FINANCE_CONTACT).build();

    PartnerOrganisationResource partnerOrg = new PartnerOrganisationResource();

    @Captor
    ArgumentCaptor<NoteResource> saveNoteArgumentCaptor;

    @Before public void setup() {
        super.setUp();
        this.setupCookieUtil();
        // populate viewmodel
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(organisationService.getOrganisationById(applicantOrganisationId)).thenReturn(leadOrganisationResource);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisationResource);
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(Arrays.asList(projectUser));
        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId)).thenReturn(partnerOrg);
    }

    @Test
    public void testViewNewNote() throws Exception {

        Cookie formCookie;
        FinanceChecksNotesAddNoteForm form = new FinanceChecksNotesAddNoteForm();
        form.setNote("Note");
        formCookie = createFormCookie(form);

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note")
                .cookie(formCookie))
                .andExpect(view().name("project/financecheck/new-note"))
                .andReturn();

        FinanceChecksNotesAddNoteViewModel noteViewModel = (FinanceChecksNotesAddNoteViewModel) result.getModelAndView().getModel().get("model");
        FinanceChecksNotesAddNoteForm modelForm = (FinanceChecksNotesAddNoteForm) result.getModelAndView().getModel().get("form");

        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(Arrays.asList(projectId, applicantOrganisationId, loggedInUser.getId())), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_note_origin"));

        assertEquals("Org1", noteViewModel.getOrganisationName());
        assertEquals("Project1", noteViewModel.getProjectName());
        assertEquals(applicantOrganisationId, noteViewModel.getOrganisationId());
        assertEquals(projectId, noteViewModel.getProjectId());
        assertEquals("/project/{projectId}/finance-check/organisation/{organisationId}/note/new-note", noteViewModel.getBaseUrl());
        assertEquals(4000, noteViewModel.getMaxNoteCharacters());
        assertEquals(400, noteViewModel.getMaxNoteWords());
        assertEquals(255, noteViewModel.getMaxTitleCharacters());
        assertTrue(noteViewModel.isLeadPartnerOrganisation());
        assertEquals(0, noteViewModel.getNewAttachmentLinks().size());
        assertEquals("Note", modelForm.getNote());
    }

    @Test
    public void testViewNewNoteInvalidOrganisation() throws Exception {

        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId + 1)).thenThrow(new ObjectNotFoundException());

        mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + (applicantOrganisationId + 1)+ "/note/new-note"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testSaveNewNote() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.saveNote(any(NoteResource.class))).thenReturn(ServiceResult.serviceSuccess(1L));

        FinanceChecksNotesAddNoteForm formIn = new FinanceChecksNotesAddNoteForm();
        Cookie formCookie = createFormCookie(formIn);
        Cookie originCookie = createOriginCookie();

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("noteTitle", "Title")
                .param("note", "Query text")
                .cookie(formCookie)
                .cookie(originCookie))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note**"))
                .andReturn();

        verify(financeCheckServiceMock).saveNote(saveNoteArgumentCaptor.capture());

        assertEquals(1, saveNoteArgumentCaptor.getAllValues().get(0).posts.size());
        assertEquals("Title", saveNoteArgumentCaptor.getAllValues().get(0).title);
        assertTrue(ZonedDateTime.now().compareTo(saveNoteArgumentCaptor.getAllValues().get(0).createdOn) >= 0);
        assertEquals("Query text", saveNoteArgumentCaptor.getAllValues().get(0).posts.get(0).body);
        assertEquals(loggedInUser, saveNoteArgumentCaptor.getAllValues().get(0).posts.get(0).author);
        assertEquals(0, saveNoteArgumentCaptor.getAllValues().get(0).posts.get(0).attachments.size());
        assertTrue(ZonedDateTime.now().compareTo(saveNoteArgumentCaptor.getAllValues().get(0).createdOn) >= 0);

        FinanceChecksNotesAddNoteForm form = (FinanceChecksNotesAddNoteForm) result.getModelAndView().getModel().get("form");
        assertEquals("Title", form.getNoteTitle());
        assertEquals("Query text", form.getNote());
        assertEquals(null, form.getAttachment());

        Optional<Cookie> cookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_notes_new_note_attachments_" + projectId + "_" + applicantOrganisationId))
                .findAny();
        assertEquals(true, cookieFound.get().getValue().isEmpty());

        Optional<Cookie> formCookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_notes_new_note_form_" + projectId + "_" + applicantOrganisationId))
                .findAny();
        assertEquals(true, formCookieFound.get().getValue().isEmpty());
    }

    @Test
    public void testSaveNewNoteNoOriginCookie() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.saveNote(any(NoteResource.class))).thenReturn(ServiceResult.serviceSuccess(1L));

        FinanceChecksNotesAddNoteForm formIn = new FinanceChecksNotesAddNoteForm();
        Cookie formCookie = createFormCookie(formIn);

        mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("noteTitle", "Title")
                .param("note", "Query text")
                .cookie(formCookie))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSaveNewNoteNoFieldsSet() throws Exception {

        Cookie originCookie = createOriginCookie();

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("noteTitle", "")
                .param("note", "")
                .cookie(originCookie))
                .andExpect(view().name("project/financecheck/new-note"))
                .andReturn();


        FinanceChecksNotesAddNoteForm form = (FinanceChecksNotesAddNoteForm) result.getModelAndView().getModel().get("form");
        assertEquals("", form.getNoteTitle());
        assertEquals("", form.getNote());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("noteTitle"));
        assertEquals("This field cannot be left blank.", bindingResult.getFieldError("noteTitle").getDefaultMessage());
        assertTrue(bindingResult.hasFieldErrors("note"));
        assertEquals("This field cannot be left blank.", bindingResult.getFieldError("note").getDefaultMessage());
    }

    @Test
    public void testSaveNewNoteFieldsTooLong() throws Exception {

        String tooLong = StringUtils.leftPad("a", 4001, 'a');
        Cookie originCookie = createOriginCookie();

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("noteTitle", tooLong)
                .param("note", tooLong)
                .cookie(originCookie))
                .andExpect(view().name("project/financecheck/new-note"))
                .andReturn();


        FinanceChecksNotesAddNoteForm form = (FinanceChecksNotesAddNoteForm) result.getModelAndView().getModel().get("form");
        assertEquals(tooLong, form.getNoteTitle());
        assertEquals(tooLong, form.getNote());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("noteTitle"));
        assertEquals("This field cannot contain more than {1} characters.", bindingResult.getFieldError("noteTitle").getDefaultMessage());
        assertTrue(bindingResult.hasFieldErrors("note"));
        assertEquals("This field cannot contain more than {1} characters.", bindingResult.getFieldError("note").getDefaultMessage());
    }

    @Test
    public void testSaveNewNoteTooManyWords() throws Exception {

        String tooManyWords = StringUtils.leftPad("a ", 802, "a ");
        Cookie originCookie = createOriginCookie();

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("noteTitle", "Title")
                .param("note", tooManyWords)
                .cookie(originCookie))
                .andExpect(view().name("project/financecheck/new-note"))
                .andReturn();


        FinanceChecksNotesAddNoteForm form = (FinanceChecksNotesAddNoteForm) result.getModelAndView().getModel().get("form");
        assertEquals("Title", form.getNoteTitle());
        assertEquals(tooManyWords, form.getNote());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("note"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("note").getDefaultMessage());
    }

    @Test
    public void testSaveNewNoteAttachment() throws Exception {

        MockMultipartFile uploadedFile = new MockMultipartFile("attachment", "testFile.pdf", "application/pdf", "My content!".getBytes());
        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L, null);
        Cookie originCookie = createOriginCookie();

        when(financeCheckServiceMock.uploadFile(projectId, "application/pdf", 11, "testFile.pdf", "My content!".getBytes())).thenReturn(ServiceResult.serviceSuccess(attachment));
        when(financeCheckServiceMock.getAttachment(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

        MvcResult result = mockMvc.perform(
                fileUpload("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note").
                        file(uploadedFile).param("uploadAttachment", "").cookie(originCookie))
                .andExpect(cookie().exists("finance_checks_notes_new_note_attachments_" + projectId + "_" + applicantOrganisationId))
                .andExpect(cookie().exists("finance_checks_notes_new_note_form_" + projectId + "_" + applicantOrganisationId))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note**"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        expectedAttachmentIds.add(1L);
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_note_attachments_" + projectId + "_" + applicantOrganisationId));

        FinanceChecksNotesAddNoteForm expectedForm = new FinanceChecksNotesAddNoteForm();
        expectedForm.setAttachment(uploadedFile);
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedForm), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_note_form_" + projectId + "_" + applicantOrganisationId));

        verify(financeCheckServiceMock).uploadFile(projectId, "application/pdf", 11, "testFile.pdf", "My content!".getBytes());
    }

    @Test
    public void testDownloadAttachmentFailsNoContent() throws Exception {

        when(projectService.getPartnerOrganisation(projectId,applicantOrganisationId)).thenThrow(new ObjectNotFoundException());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note/attachment/1"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404"))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testDownloadAttachmentFailsInvalidOrganisation() throws Exception {

        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId + 1)).thenThrow(new ObjectNotFoundException());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + (applicantOrganisationId + 1) + "/note/new-note/attachment/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testCancelNewNote() throws Exception {

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie ck = createAttachmentsCookie(attachmentIds);

        FinanceChecksNotesAddNoteForm formIn = new FinanceChecksNotesAddNoteForm();
        Cookie formCookie = createFormCookie(formIn);

        when(financeCheckServiceMock.deleteFile(1L)).thenReturn(ServiceResult.serviceSuccess());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note/cancel")
                    .cookie(ck)
                    .cookie(formCookie))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note**"))
                .andReturn();

        Optional<Cookie> cookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_notes_new_note_attachments_" + projectId + "_" + applicantOrganisationId))
                .findAny();
        assertEquals(true, cookieFound.get().getValue().isEmpty());

        Optional<Cookie> formCookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_notes_new_note_form_" + projectId + "_" + applicantOrganisationId))
                .findAny();
        assertEquals(true, formCookieFound.get().getValue().isEmpty());

        Optional<Cookie> originCookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_notes_new_note_origin"))
                .findAny();
        assertEquals(true, originCookieFound.get().getValue().isEmpty());

        verify(financeCheckServiceMock).deleteFile(1L);
    }

    @Test
    public void testCancelNewNoteInvalidOrganisation() throws Exception {

        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId + 1)).thenThrow(new ObjectNotFoundException());

        mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + (applicantOrganisationId + 1) + "/note/new-note/cancel"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testViewNewNoteWithAttachments() throws Exception {

        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L, null);
        when(financeCheckServiceMock.getAttachment(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        Cookie cookie = new Cookie("finance_checks_notes_new_note_attachments_" + projectId + "_" + applicantOrganisationId, encryptedData);
        Cookie originCookie = createOriginCookie();
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note")
                .cookie(cookie)
                .cookie(originCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("project/financecheck/new-note"))
                .andReturn();

        FinanceChecksNotesAddNoteViewModel noteViewModel = (FinanceChecksNotesAddNoteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Org1", noteViewModel.getOrganisationName());
        assertEquals("Project1", noteViewModel.getProjectName());
        assertEquals(applicantOrganisationId, noteViewModel.getOrganisationId());
        assertEquals(projectId, noteViewModel.getProjectId());
        assertEquals("/project/{projectId}/finance-check/organisation/{organisationId}/note/new-note", noteViewModel.getBaseUrl());
        assertEquals(4000, noteViewModel.getMaxNoteCharacters());
        assertEquals(400, noteViewModel.getMaxNoteWords());
        assertEquals(255, noteViewModel.getMaxTitleCharacters());
        assertTrue(noteViewModel.isLeadPartnerOrganisation());
        assertEquals(1, noteViewModel.getNewAttachmentLinks().size());
        assertEquals("name", noteViewModel.getNewAttachmentLinks().get(1L));
    }

    @Test
    public void testViewNewNoteWithAttachmentsInvalidOrganisation() throws Exception {

        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId + 1)).thenThrow(new ObjectNotFoundException());

        mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + (applicantOrganisationId + 1) + "/note/new-note"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRemoveAttachment() throws Exception {

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie ck = createAttachmentsCookie(attachmentIds);
        Cookie originCookie = createOriginCookie();

        when(financeCheckServiceMock.deleteFile(1L)).thenReturn(ServiceResult.serviceSuccess());

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note")
                .param("removeAttachment", "1")
                .cookie(ck)
                .cookie(originCookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("noteTitle", "Title")
                .param("note", "Query"))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note**"))
                .andExpect(cookie().exists("finance_checks_notes_new_note_attachments_" + projectId + "_" + applicantOrganisationId))
                .andExpect(cookie().exists("finance_checks_notes_new_note_form_" + projectId + "_" + applicantOrganisationId))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_note_attachments_" + projectId + "_" + applicantOrganisationId));

        FinanceChecksNotesAddNoteForm expectedForm = new FinanceChecksNotesAddNoteForm();
        expectedForm.setNote("Query");
        expectedForm.setNoteTitle("Title");
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedForm), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_note_form_" + projectId + "_" + applicantOrganisationId));

        verify(financeCheckServiceMock).deleteFile(1L);

        FinanceChecksNotesAddNoteForm form = (FinanceChecksNotesAddNoteForm) result.getModelAndView().getModel().get("form");
        assertEquals("Title", form.getNoteTitle());
        assertEquals("Query", form.getNote());
        assertEquals(null, form.getAttachment());
    }

    @Test
    public void testRemoveAttachmentInvalidOriginCookie() throws Exception {

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie ck = createAttachmentsCookie(attachmentIds);
        List<Long> originData = Arrays.asList(projectId, applicantOrganisationId+1, loggedInUser.getId());
        String cookieContent = JsonUtil.getSerializedObject(originData);
        String encryptedContent = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        Cookie originCookie = new Cookie("finance_checks_notes_new_note_origin", encryptedContent);

        mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note")
                .param("removeAttachment", "1")
                .cookie(ck)
                .cookie(originCookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("noteTitle", "Title")
                .param("note", "Query"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testRemoveAttachmentDoesNotRemoveAttachmentNotInCookie() throws Exception {

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie ck = createAttachmentsCookie(attachmentIds);
        Cookie originCookie = createOriginCookie();

        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L, null);

        when(financeCheckServiceMock.getAttachment(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note")
                .param("removeAttachment", "2")
                .cookie(ck)
                .cookie(originCookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("noteTitle", "Title")
                .param("note", "Query"))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/new-note**"))
                .andReturn();

        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(attachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_note_attachments_" + projectId + "_" + applicantOrganisationId));

        FinanceChecksNotesAddNoteForm form = (FinanceChecksNotesAddNoteForm) result.getModelAndView().getModel().get("form");
        assertEquals("Title", form.getNoteTitle());
        assertEquals("Query", form.getNote());
        assertEquals(null, form.getAttachment());
    }

    private Cookie createAttachmentsCookie(List<Long> attachmentIds) throws Exception {
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie("finance_checks_notes_new_note_attachments_" + projectId + "_" + applicantOrganisationId, encryptedData);
    }

    private Cookie createFormCookie(FinanceChecksNotesAddNoteForm form) throws Exception {
        String cookieContent = JsonUtil.getSerializedObject(form);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie("finance_checks_notes_new_note_form_" + projectId + "_" + applicantOrganisationId, encryptedData);
    }

    private Cookie createOriginCookie() throws Exception {
        List<Long> originData = Arrays.asList(projectId, applicantOrganisationId, loggedInUser.getId());
        String cookieContent = JsonUtil.getSerializedObject(originData);
        String encryptedContent = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie("finance_checks_notes_new_note_origin", encryptedContent);
    }

    @Override
    protected FinanceChecksNotesAddNoteController supplyControllerUnderTest() {
        return new FinanceChecksNotesAddNoteController();
    }
}
