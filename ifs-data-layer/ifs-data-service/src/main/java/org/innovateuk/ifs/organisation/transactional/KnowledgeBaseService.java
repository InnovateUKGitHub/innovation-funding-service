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
    @SecuredBySpring(value = "TODO", description = "TODO")
    ServiceResult<List<String>> getKnowledegeBases();

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "TODO", description = "TODO")
    ServiceResult<String> getKnowledegeBase(long id);

    @PreAuthorize("hasAuthority('system_maintainer')")
    @SecuredBySpring(value = "CREATE_KNOWLEDGE_BASE", description = "System maintainer will create knowledege bases" )
    ServiceResult<Long> createKnowledgeBase(String knowledegeBase);

    @PreAuthorize("hasAuthority('system_maintainer')")
    @SecuredBySpring(value = "DELETE_KNOWLEDGE_BASE", description = "System maintainer will delete knowledge base" )
    ServiceResult<Void> deleteKnowledgeBase(long id);

}
