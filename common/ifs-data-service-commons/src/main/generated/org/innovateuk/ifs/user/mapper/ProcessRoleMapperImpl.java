package org.innovateuk.ifs.user.mapper;

import java.util.ArrayList;
import javax.annotation.Generated;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-09-29T08:10:06+0100",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 11.0.15 (Eclipse Adoptium)"
)
@Component
public class ProcessRoleMapperImpl extends ProcessRoleMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Iterable<ProcessRoleResource> mapToResource(Iterable<ProcessRole> domain) {
        if ( domain == null ) {
            return new ArrayList<ProcessRoleResource>();
        }

        ArrayList<ProcessRoleResource> iterable = new ArrayList<ProcessRoleResource>();
        for ( ProcessRole processRole : domain ) {
            iterable.add( mapToResource( processRole ) );
        }

        return iterable;
    }

    @Override
    public ProcessRole mapToDomain(ProcessRoleResource resource) {

        ProcessRole processRole = new ProcessRole();

        if ( resource != null ) {
            if ( resource.getApplicationId() != null ) {
                processRole.setApplicationId( resource.getApplicationId() );
            }
            processRole.setRole( resource.getRole() );
            processRole.setOrganisationId( resource.getOrganisationId() );
            processRole.setUser( userMapper.mapIdToDomain( resource.getUser() ) );
            processRole.setId( resource.getId() );
        }

        return processRole;
    }

    @Override
    public Iterable<ProcessRole> mapToDomain(Iterable<ProcessRoleResource> resource) {
        if ( resource == null ) {
            return new ArrayList<ProcessRole>();
        }

        ArrayList<ProcessRole> iterable = new ArrayList<ProcessRole>();
        for ( ProcessRoleResource processRoleResource : resource ) {
            iterable.add( mapToDomain( processRoleResource ) );
        }

        return iterable;
    }

    @Override
    public ProcessRoleResource mapToResource(ProcessRole domain) {

        ProcessRoleResource processRoleResource = new ProcessRoleResource();

        if ( domain != null ) {
            String name = domainUserName( domain );
            if ( name != null ) {
                processRoleResource.setUserName( name );
            }
            String email = domainUserEmail( domain );
            if ( email != null ) {
                processRoleResource.setUserEmail( email );
            }
            processRoleResource.setUser( userMapper.mapUserToId( domain.getUser() ) );
            processRoleResource.setApplicationId( domain.getApplicationId() );
            processRoleResource.setRole( domain.getRole() );
            processRoleResource.setOrganisationId( domain.getOrganisationId() );
            processRoleResource.setId( domain.getId() );
        }

        return processRoleResource;
    }

    private String domainUserName(ProcessRole processRole) {
        if ( processRole == null ) {
            return null;
        }
        User user = processRole.getUser();
        if ( user == null ) {
            return null;
        }
        String name = user.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String domainUserEmail(ProcessRole processRole) {
        if ( processRole == null ) {
            return null;
        }
        User user = processRole.getUser();
        if ( user == null ) {
            return null;
        }
        String email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }
}
