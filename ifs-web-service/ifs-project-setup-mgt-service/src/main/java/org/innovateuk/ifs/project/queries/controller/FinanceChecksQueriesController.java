package org.innovateuk.ifs.project.queries.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.notesandqueries.resource.post.PostAttachmentResource;
import org.innovateuk.ifs.notesandqueries.resource.post.PostResource;
import org.innovateuk.ifs.notesandqueries.resource.thread.FinanceChecksSectionType;
import org.innovateuk.ifs.notesandqueries.resource.thread.ThreadResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesAttachmentResourceViewModel;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesPostViewModel;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesQueryViewModel;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This Controller handles finance check queries activity for the finance team members
 */
@Controller
@RequestMapping(FinanceChecksQueriesController.FINANCE_CHECKS_QUERIES_BASE_URL)
public class FinanceChecksQueriesController {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private CookieUtil cookieUtil;

    public static final String FINANCE_CHECKS_QUERIES_BASE_URL = "/project/{projectId}/finance-check/organisation/{organisationId}/query";

    private static final String ATTACHMENT_COOKIE = "finance_checks_queries_new_attachments";
    private static final String UNKNOWN_FIELD = "Unknown";


    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(method = GET)
    public String showPage(@PathVariable Long projectId,
                           @PathVariable Long organisationId,
                           @RequestParam(value = "query_section", required = false) String querySection,
                           Model model) {
        FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, querySection);
        model.addAttribute("model", viewModel);
        return "project/financecheck/queries";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value = "/attachment/{attachmentId}", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long projectId,
                                                         @PathVariable Long organisationId,
                                                         @PathVariable Long attachmentId,
                                                         @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                                         HttpServletRequest request) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        Optional<ByteArrayResource> content = Optional.empty();
        Optional<FileEntryResource> fileDetails = Optional.empty();

        if (attachments.contains(attachmentId)) {
            // TODO get file from service
        }
        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }

    private List<FinanceChecksQueriesQueryViewModel> loadQueryModel(Long projectId, Long organisationId) {
        // Dummy test data
        ThreadResource thread = new ThreadResource();
        PostResource firstPost = new PostResource();
        PostResource firstResponse = new PostResource();
        thread.setCreatedOn(LocalDateTime.now());
        thread.setAwaitingResponse(false);
        thread.setOrganisationId(22L);
        thread.setProjectId(3L);
        thread.setTitle("Query title");
        thread.setSectionType(FinanceChecksSectionType.ELIGIBILITY);
        thread.setId(1L);
        firstPost.setCreatedOn(LocalDateTime.now().plusMinutes(10L));
        firstPost.setUserId(18L);
        firstPost.setPostBody("Question");
        firstResponse.setCreatedOn(LocalDateTime.now().plusMinutes(20L));
        firstResponse.setUserId(55L);
        firstResponse.setPostBody("Response");
        firstResponse.setAttachments(new LinkedList<>());
        PostAttachmentResource att1 = new PostAttachmentResource();
        att1.setFileEntryId(23L);
        firstPost.setAttachments(Arrays.asList(att1));
        thread.setPosts(Arrays.asList(firstPost, firstResponse));

        ThreadResource thread2 = new ThreadResource();
        PostResource firstPost2 = new PostResource();
        thread2.setCreatedOn(LocalDateTime.now());
        thread2.setAwaitingResponse(true);
        thread2.setOrganisationId(22L);
        thread2.setProjectId(3L);
        thread2.setTitle("Query2 title");
        thread2.setSectionType(FinanceChecksSectionType.ELIGIBILITY);
        thread2.setId(3L);
        firstPost2.setCreatedOn(LocalDateTime.now().plusMinutes(10L));
        firstPost2.setUserId(18L);
        firstPost2.setPostBody("Question2");
        firstPost2.setAttachments(new LinkedList<>());
        thread2.setPosts (Arrays.asList(firstPost2));
        List<ThreadResource> queries = Arrays.asList(thread2, thread);

        // TODO read data from service

        // order queries by most recent post
        List<ThreadResource> sortedQueries = queries.stream().
                flatMap(t -> t.getPosts().stream()
                        .map(p -> new AbstractMap.SimpleImmutableEntry<>(t, p)))
                .sorted((e1, e2) -> e2.getValue().getCreatedOn().compareTo(e1.getValue().getCreatedOn()))
                .map(m -> m.getKey())
                .distinct()
                .collect(Collectors.toList());

        List<FinanceChecksQueriesQueryViewModel> queryModel = new LinkedList<>();
        Long attachmentIndex = 0L;
        for (ThreadResource t : sortedQueries) {
            List<FinanceChecksQueriesPostViewModel> posts = new LinkedList<>();
            for (PostResource p : t.getPosts()) {
                List<FinanceChecksQueriesAttachmentResourceViewModel> attachments = new LinkedList<>();
                for (PostAttachmentResource a : p.getAttachments()) {
                    FinanceChecksQueriesAttachmentResourceViewModel attachment = new FinanceChecksQueriesAttachmentResourceViewModel();
                    // TODO get file details from service
                    attachment.setFileEntryId(a.getFileEntryId());
                    attachment.setPostId(a.getPostId());
                    attachment.setFilename("file" + attachmentIndex.toString());
                    attachment.setLocalFileId(attachmentIndex);
                    attachments.add(attachment);
                    attachmentIndex++;
                    //FileEntryResource f;
                    //f.getName();
                }
                UserResource user = userService.findById(p.getUserId());
                OrganisationResource organisation = organisationService.getOrganisationForUser(p.getUserId());
                FinanceChecksQueriesPostViewModel post = new FinanceChecksQueriesPostViewModel();
                post.setViewModelAttachments(attachments);
                post.setUsername(user.getName() + " - " + organisation.getName() + (user.hasRole(UserRoleType.PROJECT_FINANCE)?  " (Finance team)" : ""));
                post.setCreatedOn(p.getCreatedOn());
                post.setPostBody(p.getPostBody());
                post.setUserId(p.getUserId());
                post.setAttachments(p.getAttachments());
                posts.add(post);
            }
            FinanceChecksQueriesQueryViewModel detail = new FinanceChecksQueriesQueryViewModel();
            detail.setViewModelPosts(posts);
            detail.setSectionType(t.getSectionType());
            detail.setCreatedOn(t.getCreatedOn());
            detail.setAwaitingResponse(t.isAwaitingResponse());
            detail.setTitle(t.getTitle());
            detail.setId(t.getId());
            detail.setProjectId(t.getProjectId());
            detail.setOrganisationId(t.getOrganisationId());
            detail.setPosts(t.getPosts());
            queryModel.add(detail);
        }
        return queryModel;
    }

    private FinanceChecksQueriesViewModel populateQueriesViewModel(Long projectId, Long organisationId, String querySection) {

        ProjectResource project = projectService.getById(projectId);

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        Optional<ProjectUserResource> financeContact = getFinanceContact(projectId, organisationId);

        return new FinanceChecksQueriesViewModel(
                organisation.getName(),
                leadPartnerOrganisation,
                financeContact.isPresent() ? financeContact.get().getUserName() : UNKNOWN_FIELD,
                financeContact.isPresent() ? financeContact.get().getEmail() : UNKNOWN_FIELD,
                financeContact.isPresent() ? financeContact.get().getPhoneNumber() : UNKNOWN_FIELD,
                querySection == null ? UNKNOWN_FIELD : querySection,
                project.getId(),
                project.getName(),
                loadQueryModel(projectId, organisationId),
                organisationId,
                FINANCE_CHECKS_QUERIES_BASE_URL
        );
    }

    private Optional<ProjectUserResource> getFinanceContact(Long projectId, Long organisationId){
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pr -> pr.isFinanceContact() && organisationId.equals(pr.getOrganisation()));
    }

    private String redirectToQueryPage(Long projectId, Long organisationId, String querySection) {
        return "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/query?query_section=" + querySection;
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
        }
    }

    private String getCookieName(Long projectId, Long organisationId) {
        return ATTACHMENT_COOKIE + "_" + projectId + "_" + organisationId;
    }

    private void saveAttachmentsToCookie(HttpServletResponse response, List<Long> attachmentFileIds, Long projectId, Long organisationId) {
        attachmentFileIds.add(projectId);
        attachmentFileIds.add(organisationId);
        String jsonState = JsonUtil.getSerializedObject(attachmentFileIds);
        cookieUtil.saveToCookie(response, getCookieName(projectId,organisationId), jsonState);
    }

    private List<Long> loadAttachmentsFromCookie(HttpServletRequest request, Long projectId, Long organisationId) {

        List<Long> attachments = new LinkedList<>();
        String json = cookieUtil.getCookieValue(request, getCookieName(projectId, organisationId));

        if (json != null && !"".equals(json)) {
            TypeReference<List<Long>> listType = new TypeReference<List<Long>>() {};
            ObjectMapper mapper = new ObjectMapper();
            try {
                attachments = mapper.readValue(json, listType);
                return attachments;
            } catch (IOException e) {
                //ignored
            }
        }
        return attachments;
    }
}
