package org.innovateuk.ifs.project.notes;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;

import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.notes.controller.FinanceChecksNotesController;
import org.innovateuk.ifs.project.notes.form.FinanceChecksNotesAddCommentForm;
import org.innovateuk.ifs.project.notes.viewmodel.FinanceChecksNotesViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
//import org.innovateuk.ifs.project.util.InternalUserOrganisationUtil;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.util.JsonUtil;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.NoteResource;
import org.innovateuk.threads.resource.PostResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceChecksNotesControllerTest extends BaseControllerMockMVCTest<FinanceChecksNotesController> {

/*    @Mock
    public InternalUserOrganisationUtil internalUserOrganisationUtil;*/

    private Long projectId = 3L;
    private Long financeTeamUserId = 18L;
    private Long applicantFinanceContactUserId = 55L;
    private Long innovateOrganisationId = 11L;
    private Long applicantOrganisationId = 22L;
    private Long projectFinanceId = 45L;
    private Long noteId = 1L;

    ApplicationResource applicationResource = newApplicationResource().build();
    ProjectResource projectResource = newProjectResource().withId(projectId).withName("Project1").withApplication(applicationResource).build();

    OrganisationResource innovateOrganisationResource = newOrganisationResource().withName("Innovate").withId(innovateOrganisationId).build();

    OrganisationResource leadOrganisationResource = newOrganisationResource().withName("Org1").withId(applicantOrganisationId).build();

    ProjectUserResource projectUser = newProjectUserResource().withOrganisation(applicantOrganisationId).withUserName("User1").withEmail("e@mail.com").withPhoneNumber("0117").withRoleName(UserRoleType.FINANCE_CONTACT).build();

    RoleResource financeTeamRole = newRoleResource().withType(PROJECT_FINANCE).build();
    UserResource financeTeamUser = newUserResource().withFirstName("A").withLastName("Z").withId(financeTeamUserId).withRolesGlobal(Arrays.asList(financeTeamRole)).build();
    UserResource projectManagerUser = newUserResource().withFirstName("B").withLastName("Z").withId(applicantFinanceContactUserId).build();

    NoteResource thread;
    NoteResource thread2;
    NoteResource thread3;

    List<NoteResource> notes;

    PartnerOrganisationResource partnerOrg = new PartnerOrganisationResource();

    @Captor
    ArgumentCaptor<PostResource> savePostArgumentCaptor;

    @Before
    public void setup() {
        super.setUp();
        this.setupCookieUtil();
        when(userService.findById(financeTeamUserId)).thenReturn(financeTeamUser);
        when(organisationService.getOrganisationForUser(financeTeamUserId)).thenReturn(innovateOrganisationResource);
        when(userService.findById(applicantFinanceContactUserId)).thenReturn(projectManagerUser);
        when(organisationService.getOrganisationForUser(applicantFinanceContactUserId)).thenReturn(leadOrganisationResource);
        when(userService.findById(applicantFinanceContactUserId)).thenReturn(projectManagerUser);
        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId)).thenReturn(partnerOrg);

        // populate viewmodel
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(organisationService.getOrganisationById(applicantOrganisationId)).thenReturn(leadOrganisationResource);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisationResource);
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(asList(projectUser));

        UserResource user1 = new UserResource();
        user1.setId(18L);
        PostResource firstPost = new PostResource(null, user1, "Question", Arrays.asList(new AttachmentResource(23L, "file1.txt", "txt", 1L, null)), ZonedDateTime.now().plusMinutes(10L));
        UserResource user2 = new UserResource();
        user2.setId(55L);
        PostResource firstResponse = new PostResource(null, user2, "Response", new ArrayList<>(), ZonedDateTime.now().plusMinutes(20L));
        thread = new NoteResource(1L, projectFinanceId, Arrays.asList(firstPost, firstResponse), "Query title", ZonedDateTime.now());

        PostResource firstPost2 = new PostResource(null, user1, "Question2", new ArrayList<>(), ZonedDateTime.now().plusMinutes(15L));
        thread2 = new NoteResource(3L, projectFinanceId, Arrays.asList(firstPost2), "Query2 title", ZonedDateTime.now());

        PostResource firstPost1 = new PostResource(null, user1, "Question3", new ArrayList<>(), ZonedDateTime.now());
        PostResource firstResponse1 = new PostResource(null, user2, "Response3", new ArrayList<>(), ZonedDateTime.now().plusMinutes(10L));

        thread3 = new NoteResource(5L, projectFinanceId, Arrays.asList(firstPost1, firstResponse1), "Query title3", ZonedDateTime.now());

        notes = Arrays.asList(thread2, thread, thread3);

        //TODO - Getting the organisation name this way is just a workaround till IFS-651 is fixed.
/*        when(internalUserOrganisationUtil.getOrganisationName(financeTeamUser, firstPost)).thenReturn(innovateOrganisationResource.getName());
        when(internalUserOrganisationUtil.getOrganisationName(financeTeamUser, firstPost2)).thenReturn(innovateOrganisationResource.getName());
        when(internalUserOrganisationUtil.getOrganisationName(financeTeamUser, firstPost1)).thenReturn(innovateOrganisationResource.getName());
        when(internalUserOrganisationUtil.getOrganisationName(projectManagerUser, firstResponse)).thenReturn(leadOrganisationResource.getName());
        when(internalUserOrganisationUtil.getOrganisationName(projectManagerUser, firstResponse1)).thenReturn(leadOrganisationResource.getName());*/
    }

    @Test
    public void testGetReadOnlyView() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.loadNotes(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(notes));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note"))
                .andExpect(view().name("project/financecheck/notes"))
                .andReturn();

        FinanceChecksNotesViewModel noteViewModel = (FinanceChecksNotesViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Org1", noteViewModel.getOrganisationName());
        assertEquals("Project1", noteViewModel.getProjectName());
        assertEquals(applicantOrganisationId, noteViewModel.getOrganisationId());
        assertEquals(projectId, noteViewModel.getProjectId());

        assertEquals(3, noteViewModel.getNotes().size());
        assertEquals("Query title", noteViewModel.getNotes().get(0).getTitle());
        assertEquals(applicantOrganisationId, noteViewModel.getNotes().get(0).getOrganisationId());
        assertEquals(projectId, noteViewModel.getNotes().get(0).getProjectId());
        assertEquals(1L, noteViewModel.getNotes().get(0).getId().longValue());
        assertEquals(2, noteViewModel.getNotes().get(0).getViewModelPosts().size());
        assertEquals("Question", noteViewModel.getNotes().get(0).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUserId, noteViewModel.getNotes().get(0).getViewModelPosts().get(0).author.getId());
        assertEquals("A Z - Innovate UK (Finance team)", noteViewModel.getNotes().get(0).getViewModelPosts().get(0).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(10L).isAfter(noteViewModel.getNotes().get(0).getViewModelPosts().get(0).createdOn));
        assertEquals(1, noteViewModel.getNotes().get(0).getViewModelPosts().get(0).attachments.size());
        assertEquals(23L, noteViewModel.getNotes().get(0).getViewModelPosts().get(0).attachments.get(0).id.longValue());
        assertEquals("file1.txt", noteViewModel.getNotes().get(0).getViewModelPosts().get(0).attachments.get(0).name);
        assertEquals("Response", noteViewModel.getNotes().get(0).getViewModelPosts().get(1).body);
        assertEquals(applicantFinanceContactUserId, noteViewModel.getNotes().get(0).getViewModelPosts().get(1).author.getId());
        assertEquals("B Z - Innovate UK", noteViewModel.getNotes().get(0).getViewModelPosts().get(1).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(20L).isAfter(noteViewModel.getNotes().get(0).getViewModelPosts().get(1).createdOn));
        assertEquals(0, noteViewModel.getNotes().get(0).getViewModelPosts().get(1).attachments.size());
        assertEquals("Query2 title", noteViewModel.getNotes().get(1).getTitle());
        assertEquals(applicantOrganisationId, noteViewModel.getNotes().get(1).getOrganisationId());
        assertEquals(projectId, noteViewModel.getNotes().get(1).getProjectId());
        assertEquals(3L, noteViewModel.getNotes().get(1).getId().longValue());
        assertEquals(1, noteViewModel.getNotes().get(1).getViewModelPosts().size());
        assertEquals("Question2", noteViewModel.getNotes().get(1).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUserId, noteViewModel.getNotes().get(1).getViewModelPosts().get(0).author.getId());
        assertEquals("A Z - Innovate UK (Finance team)", noteViewModel.getNotes().get(1).getViewModelPosts().get(0).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(15L).isAfter(noteViewModel.getNotes().get(1).getViewModelPosts().get(0).createdOn));
        assertEquals(0, noteViewModel.getNotes().get(1).getViewModelPosts().get(0).attachments.size());

        assertEquals("Query title3", noteViewModel.getNotes().get(2).getTitle());
        assertEquals(applicantOrganisationId, noteViewModel.getNotes().get(2).getOrganisationId());
        assertEquals(projectId, noteViewModel.getNotes().get(2).getProjectId());
        assertEquals(5L, noteViewModel.getNotes().get(2).getId().longValue());
        assertEquals(2, noteViewModel.getNotes().get(2).getViewModelPosts().size());
        assertEquals("Question3", noteViewModel.getNotes().get(2).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUserId, noteViewModel.getNotes().get(2).getViewModelPosts().get(0).author.getId());
        assertEquals("A Z - Innovate UK (Finance team)", noteViewModel.getNotes().get(2).getViewModelPosts().get(0).getUsername());
        assertTrue(ZonedDateTime.now().isAfter(noteViewModel.getNotes().get(2).getViewModelPosts().get(0).createdOn));
        assertEquals(0, noteViewModel.getNotes().get(2).getViewModelPosts().get(0).attachments.size());
        assertEquals("Response3", noteViewModel.getNotes().get(2).getViewModelPosts().get(1).body);
        assertEquals(applicantFinanceContactUserId, noteViewModel.getNotes().get(2).getViewModelPosts().get(1).author.getId());
        assertEquals("B Z - Innovate UK", noteViewModel.getNotes().get(2).getViewModelPosts().get(1).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(10L).isAfter(noteViewModel.getNotes().get(2).getViewModelPosts().get(1).createdOn));
        assertEquals(0, noteViewModel.getNotes().get(2).getViewModelPosts().get(1).attachments.size());
    }

    @Test
    public void testGetReadOnlyViewInvalidOrganisationId() throws Exception {

        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId + 1L)).thenThrow(new ObjectNotFoundException());

        mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + (applicantOrganisationId + 1L) + "/note"))
                .andExpect(view().name("404"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDownloadAttachmentFailsNoContent() throws Exception {

        when(financeCheckServiceMock.downloadFile(1L)).thenThrow(new ForbiddenActionException());
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/attachment/1"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("forbidden"))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testDownloadAttachmentFailsInvalidOrganisation() throws Exception {

        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId + 1L)).thenThrow(new ObjectNotFoundException());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + (applicantOrganisationId + 1L) + "/note/attachment/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testDownloadAttachmentFailsNoInfo() throws Exception {

        ByteArrayResource bytes = new ByteArrayResource("File contents".getBytes());

        when(financeCheckServiceMock.downloadFile(1L)).thenReturn(bytes);
        when(financeCheckServiceMock.getAttachmentInfo(1L)).thenThrow(new ForbiddenActionException());
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/attachment/1"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("forbidden"))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testViewNewComment() throws Exception {

        Cookie formCookie;
        FinanceChecksNotesAddCommentForm form = new FinanceChecksNotesAddCommentForm();
        form.setComment("comment");
        formCookie = createFormCookie(form);

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.loadNotes(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(notes));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/"+ noteId +"/new-comment")
                .cookie(formCookie))
                .andExpect(view().name("project/financecheck/notes"))
                .andReturn();

        FinanceChecksNotesViewModel responseViewModel = (FinanceChecksNotesViewModel) result.getModelAndView().getModel().get("model");
        FinanceChecksNotesAddCommentForm modelForm = (FinanceChecksNotesAddCommentForm) result.getModelAndView().getModel().get("form");

        assertEquals("Org1", responseViewModel.getOrganisationName());
        assertEquals("Project1", responseViewModel.getProjectName());
        assertEquals(applicantOrganisationId, responseViewModel.getOrganisationId());
        assertEquals(projectId, responseViewModel.getProjectId());
        assertEquals(1L, responseViewModel.getNoteId().longValue());
        assertEquals("/project/{projectId}/finance-check/organisation/{organisationId}/note", responseViewModel.getBaseUrl());
        assertEquals(4000, responseViewModel.getMaxNoteCharacters());
        assertEquals(400, responseViewModel.getMaxNoteWords());
        assertTrue(responseViewModel.isLeadPartnerOrganisation());
        assertEquals(0, responseViewModel.getNewAttachmentLinks().size());
        assertEquals("comment", modelForm.getComment());

        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(Arrays.asList(projectId, applicantOrganisationId, noteId, loggedInUser.getId())), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_comment_origin"));
    }

    @Test
    public void testViewNewCommentWhenNoteIdDoesNotExist() throws Exception {

        Cookie formCookie;
        FinanceChecksNotesAddCommentForm form = new FinanceChecksNotesAddCommentForm();
        form.setComment("comment");
        formCookie = createFormCookie(form);

        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(Collections.singletonList(leadOrganisationResource));

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.loadNotes(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(Collections.emptyList()));

        mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/"+ noteId +"/new-comment")
                .cookie(formCookie))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testViewNewCommentInvalidOrganisation() throws Exception {

        Cookie formCookie;
        FinanceChecksNotesAddCommentForm form = new FinanceChecksNotesAddCommentForm();
        form.setComment("comment");
        formCookie = createFormCookie(form);

        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId + 1)).thenThrow(new ObjectNotFoundException());

        mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + (applicantOrganisationId + 1) + "/note/"+ noteId +"/new-comment")
                .cookie(formCookie))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testSaveNewComment() throws Exception {

        when(financeCheckServiceMock.saveNotePost(any(PostResource.class), eq(1L))).thenReturn(ServiceResult.serviceSuccess());

        FinanceChecksNotesAddCommentForm formIn = new FinanceChecksNotesAddCommentForm();
        Cookie formCookie = createFormCookie(formIn);
        Cookie originCookie = createOriginCookie();

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/"+ noteId +"/new-comment")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("comment", "Query text")
                .cookie(formCookie)
                .cookie(originCookie))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note**"))
                .andReturn();

        verify(financeCheckServiceMock).saveNotePost(savePostArgumentCaptor.capture(), eq(1L));

        assertEquals("Query text", savePostArgumentCaptor.getAllValues().get(0).body);
        assertEquals(loggedInUser, savePostArgumentCaptor.getAllValues().get(0).author);
        assertEquals(0, savePostArgumentCaptor.getAllValues().get(0).attachments.size());
        assertTrue(ZonedDateTime.now().compareTo(savePostArgumentCaptor.getAllValues().get(0).createdOn) >= 0);

        FinanceChecksNotesAddCommentForm form = (FinanceChecksNotesAddCommentForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query text", form.getComment());
        assertEquals(null, form.getAttachment());

        Optional<Cookie> cookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_notes_new_comment_attachments_" + projectId + "_" + applicantOrganisationId + "_" + noteId))
                .findAny();
        assertEquals(true, cookieFound.get().getValue().isEmpty());

        Optional<Cookie> formCookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_notes_new_comment_form_" + projectId + "_" + applicantOrganisationId + "_" + noteId))
                .findAny();
        assertEquals(true, formCookieFound.get().getValue().isEmpty());
    }

    @Test
    public void testSaveNewCommentNoOriginCookie() throws Exception {

        when(financeCheckServiceMock.saveNotePost(any(PostResource.class), eq(1L))).thenReturn(ServiceResult.serviceSuccess());

        FinanceChecksNotesAddCommentForm formIn = new FinanceChecksNotesAddCommentForm();
        Cookie formCookie = createFormCookie(formIn);

        mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/"+ noteId +"/new-comment")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("comment", "Query text")
                .cookie(formCookie))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testSaveNewCommentNoFieldsSet() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.loadNotes(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(notes));

        Cookie originCookie = createOriginCookie();

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/"+ noteId +"/new-comment")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("comment", "")
                .cookie(originCookie))
                .andExpect(view().name("project/financecheck/notes"))
                .andReturn();

        FinanceChecksNotesAddCommentForm form = (FinanceChecksNotesAddCommentForm) result.getModelAndView().getModel().get("form");
        assertEquals("", form.getComment());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("comment"));
        assertEquals("This field cannot be left blank.", bindingResult.getFieldError("comment").getDefaultMessage());
    }

    @Test
    public void testSaveNewCommentFieldsTooLong() throws Exception {

        String tooLong = StringUtils.leftPad("a", 4001, 'a');

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.loadNotes(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(notes));

        Cookie originCookie = createOriginCookie();

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/"+ noteId +"/new-comment")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("comment", tooLong)
                .cookie(originCookie))
                .andExpect(view().name("project/financecheck/notes"))
                .andReturn();


        FinanceChecksNotesAddCommentForm form = (FinanceChecksNotesAddCommentForm) result.getModelAndView().getModel().get("form");
        assertEquals(tooLong, form.getComment());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("comment"));
        assertEquals("This field cannot contain more than {1} characters.", bindingResult.getFieldError("comment").getDefaultMessage());
    }

    @Test
    public void testSaveNewCommentTooManyWords() throws Exception {

        String tooManyWords = StringUtils.leftPad("a ", 802, "a ");

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.loadNotes(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(notes));

        Cookie originCookie = createOriginCookie();

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/"+ noteId +"/new-comment")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("comment", tooManyWords)
                .cookie((originCookie)))
                .andExpect(view().name("project/financecheck/notes"))
                .andReturn();

        FinanceChecksNotesAddCommentForm form = (FinanceChecksNotesAddCommentForm) result.getModelAndView().getModel().get("form");
        assertEquals(tooManyWords, form.getComment());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("comment"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("comment").getDefaultMessage());
    }

    @Test
    public void testSaveNewCommentAttachment() throws Exception {

        MockMultipartFile uploadedFile = new MockMultipartFile("attachment", "testFile.pdf", "application/pdf", "My content!".getBytes());
        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L, null);

        when(financeCheckServiceMock.uploadFile(projectId, "application/pdf", 11, "testFile.pdf", "My content!".getBytes())).thenReturn(ServiceResult.serviceSuccess(attachment));
        when(financeCheckServiceMock.getAttachment(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.loadNotes(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(notes));

        Cookie originCookie = createOriginCookie();

        MvcResult result = mockMvc.perform(
                fileUpload("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/1/new-comment").
                        file(uploadedFile).
                        param("uploadAttachment", "")
                        .cookie(originCookie))
                .andExpect(cookie().exists("finance_checks_notes_new_comment_attachments_" + projectId + "_" + applicantOrganisationId + "_" +1L))
                .andExpect(cookie().exists("finance_checks_notes_new_comment_form_" + projectId + "_" + applicantOrganisationId + "_" +1L))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/" + noteId + "/new-comment**"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        expectedAttachmentIds.add(1L);
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_comment_attachments_" + projectId + "_" + applicantOrganisationId + "_" +1L));

        FinanceChecksNotesAddCommentForm expectedForm = new FinanceChecksNotesAddCommentForm();
        expectedForm.setAttachment(uploadedFile);
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedForm), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_comment_form_" + projectId + "_" + applicantOrganisationId + "_" +1L));

        FinanceChecksNotesAddCommentForm form = (FinanceChecksNotesAddCommentForm) result.getModelAndView().getModel().get("form");
        assertEquals(uploadedFile, form.getAttachment());

    }

    @Test
    public void testSaveNewCommentAttachmentInvalidOriginCookie() throws Exception {

        MockMultipartFile uploadedFile = new MockMultipartFile("attachment", "testFile.pdf", "application/pdf", "My content!".getBytes());

        List<Long> originData = Arrays.asList(projectId, applicantOrganisationId, noteId+999L, loggedInUser.getId());
        String cookieContent = JsonUtil.getSerializedObject(originData);
        String encryptedContent = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        Cookie originCookie = new Cookie("finance_checks_notes_new_comment_origin", encryptedContent);

        mockMvc.perform(
                fileUpload("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/1/new-comment").
                        file(uploadedFile).
                        param("uploadAttachment", "")
                        .cookie(originCookie))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testDownloadCommentAttachmentFailsInvalidOrganisation() throws Exception {

        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId + 1)).thenThrow(new ObjectNotFoundException());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + (applicantOrganisationId + 1) + "/note/"+ noteId +"/new-comment/attachment/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testCancelNewComment() throws Exception {

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie ck = createAttachmentsCookie(attachmentIds);

        FinanceChecksNotesAddCommentForm formIn = new FinanceChecksNotesAddCommentForm();
        Cookie formCookie = createFormCookie(formIn);

        Cookie originCookie = createOriginCookie();

        when(financeCheckServiceMock.deleteFile(1L)).thenReturn(ServiceResult.serviceSuccess());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/"+ noteId +"/new-comment/cancel")
                    .cookie(ck)
                    .cookie(formCookie)
                    .cookie(originCookie))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note**"))
                .andReturn();

        Optional<Cookie> cookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_notes_new_comment_attachments_" + projectId + "_" + applicantOrganisationId + "_" +1L))
                .findAny();
        assertEquals(true, cookieFound.get().getValue().isEmpty());

        Optional<Cookie> formCookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_notes_new_comment_form_" + projectId + "_" + applicantOrganisationId + "_" +1L))
                .findAny();
        assertEquals(true, formCookieFound.get().getValue().isEmpty());

        Optional<Cookie> originCookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_notes_new_comment_origin"))
                .findAny();
        assertEquals(true, originCookieFound.get().getValue().isEmpty());


        verify(financeCheckServiceMock).deleteFile(1L);
    }

    @Test
    public void testCancelNewCommentInvalidOrganisation() throws Exception {

        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId + 1)).thenThrow(new ObjectNotFoundException());


        mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + (applicantOrganisationId + 1) + "/note/"+ noteId +"/new-comment/cancel"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testViewNewCommentWithAttachments() throws Exception {

        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L, null);

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.loadNotes(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(notes));

        when(financeCheckServiceMock.getAttachment(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        Cookie cookie = new Cookie("finance_checks_notes_new_comment_attachments_" + projectId + "_" + applicantOrganisationId + "_" + 1L, encryptedData);
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/"+ noteId +"/new-comment")
                .cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("project/financecheck/notes"))
                .andReturn();

        FinanceChecksNotesViewModel noteViewModel = (FinanceChecksNotesViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Org1", noteViewModel.getOrganisationName());
        assertEquals("Project1", noteViewModel.getProjectName());
        assertEquals(applicantOrganisationId, noteViewModel.getOrganisationId());
        assertEquals(projectId, noteViewModel.getProjectId());
        assertEquals("/project/{projectId}/finance-check/organisation/{organisationId}/note", noteViewModel.getBaseUrl());
        assertEquals(4000, noteViewModel.getMaxNoteCharacters());
        assertEquals(400, noteViewModel.getMaxNoteWords());
        assertEquals(1L, noteViewModel.getNoteId().longValue());
        assertTrue(noteViewModel.isLeadPartnerOrganisation());
        assertEquals(1, noteViewModel.getNewAttachmentLinks().size());
        assertEquals("name", noteViewModel.getNewAttachmentLinks().get(1L));
    }

    @Test
    public void testViewNewCommentWithAttachmentsInvalidOrganisation() throws Exception {

        when(projectService.getPartnerOrganisation(projectId, applicantOrganisationId + 1)).thenThrow(new ObjectNotFoundException());

        mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + (applicantOrganisationId + 1) + "/note/"+ noteId +"/new-comment"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testRemoveAttachment() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.loadNotes(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(notes));

        when(financeCheckServiceMock.deleteFile(1L)).thenReturn(ServiceResult.serviceSuccess());

        Cookie originCookie = createOriginCookie();

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie cookie = createAttachmentsCookie(attachmentIds);
        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/" + noteId + "/new-comment/")
                .param("removeAttachment", "1")
                .cookie(cookie)
                .cookie(originCookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("comment", "Query"))
                .andExpect(cookie().exists("finance_checks_notes_new_comment_attachments_" + projectId + "_" + applicantOrganisationId + "_" + noteId))
                .andExpect(cookie().exists("finance_checks_notes_new_comment_form_" + projectId + "_" + applicantOrganisationId + "_" + noteId))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/" + noteId + "/new-comment**"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_comment_attachments_" + projectId + "_" + applicantOrganisationId + "_" + noteId));

        FinanceChecksNotesAddCommentForm expectedForm = new FinanceChecksNotesAddCommentForm();
        expectedForm.setComment("Query");
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedForm), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_comment_form_" + projectId + "_" + applicantOrganisationId + "_" + noteId));

        verify(financeCheckServiceMock).deleteFile(1L);

        FinanceChecksNotesAddCommentForm form = (FinanceChecksNotesAddCommentForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query", form.getComment());
        assertEquals(null, form.getAttachment());
    }

    @Test
    public void testRemoveAttachmentDoesNotRemoveAttachmentNotInCookie() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.loadNotes(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(notes));

        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L, null);

        when(financeCheckServiceMock.getAttachment(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie cookie = createAttachmentsCookie(attachmentIds);
        Cookie originCookie = createOriginCookie();
        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/"+ noteId +"/new-comment")
                .param("removeAttachment", "2")
                .cookie(cookie)
                .cookie(originCookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("comment", "Query"))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/" + noteId + "/new-comment**"))
                .andReturn();

        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(attachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_notes_new_comment_attachments_" + projectId + "_" + applicantOrganisationId + "_" + noteId));

        FinanceChecksNotesAddCommentForm form = (FinanceChecksNotesAddCommentForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query", form.getComment());
        assertEquals(null, form.getAttachment());
    }

    @Test
    public void testRemoveAttachmentDoesNotRemoveAttachmentNoOriginCookie() throws Exception {

        mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/note/"+ noteId +"/new-comment"))
                .andExpect(status().isNotFound());

    }

    private Cookie createAttachmentsCookie(List<Long> attachmentIds) throws Exception{
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie("finance_checks_notes_new_comment_attachments_" + projectId + "_" + applicantOrganisationId + "_" + noteId, encryptedData);
    }

    private Cookie createFormCookie(FinanceChecksNotesAddCommentForm form) throws Exception {
        String cookieContent = JsonUtil.getSerializedObject(form);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie("finance_checks_notes_new_comment_form_" + projectId + "_" + applicantOrganisationId + "_" + noteId, encryptedData);
    }

    private Cookie createOriginCookie() throws Exception {
        List<Long> originData = Arrays.asList(projectId, applicantOrganisationId, noteId, loggedInUser.getId());
        String cookieContent = JsonUtil.getSerializedObject(originData);
        String encryptedContent = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie("finance_checks_notes_new_comment_origin", encryptedContent);
    }

    @Override
    protected FinanceChecksNotesController supplyControllerUnderTest() {
        return new FinanceChecksNotesController();
    }
}
