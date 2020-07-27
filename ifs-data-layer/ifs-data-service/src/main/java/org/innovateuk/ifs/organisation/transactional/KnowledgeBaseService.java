package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 *
 */
public interface KnowledgeBaseService {

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "GET_KNOWLEDGE_BASE", description = "The System Registration user can get all Knowledge base organisations.")
    ServiceResult<List<String>> getKnowledegeBases();

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "GET_KNOWLEDGE_BASE", description = "The System Registration user can get a Knowledge base organisation searched by id")
    ServiceResult<String> getKnowledegeBase(long id);

    @PreAuthorize("hasAuthority('system_maintainer')")
    @SecuredBySpring(value = "CREATE_KNOWLEDGE_BASE", description = "System maintainer will create knowledge bases" )
    ServiceResult<Long> createKnowledgeBase(String knowledegeBase);

    @PreAuthorize("hasAuthority('system_maintainer')")
    @SecuredBySpring(value = "DELETE_KNOWLEDGE_BASE", description = "System maintainer will delete knowledge base" )
    ServiceResult<Void> deleteKnowledgeBase(long id);

}
