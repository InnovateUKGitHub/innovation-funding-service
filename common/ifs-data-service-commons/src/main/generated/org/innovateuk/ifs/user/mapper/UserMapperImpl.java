package org.innovateuk.ifs.user.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Generated;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-09-29T08:10:06+0100",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 11.0.15 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl extends UserMapper {

    @Override
    public Iterable<UserResource> mapToResource(Iterable<User> domain) {
        if ( domain == null ) {
            return new ArrayList<UserResource>();
        }

        ArrayList<UserResource> iterable = new ArrayList<UserResource>();
        for ( User user : domain ) {
            iterable.add( mapToResource( user ) );
        }

        return iterable;
    }

    @Override
    public Iterable<User> mapToDomain(Iterable<UserResource> resource) {
        if ( resource == null ) {
            return new ArrayList<User>();
        }

        ArrayList<User> iterable = new ArrayList<User>();
        for ( UserResource userResource : resource ) {
            iterable.add( mapToDomain( userResource ) );
        }

        return iterable;
    }

    @Override
    public User mapToDomain(UserResource resource) {

        User user = new User();

        if ( resource != null ) {
            user.setEdiStatus( resource.getEdiStatus() );
            user.setEdiReviewDate( resource.getEdiReviewDate() );
            user.setId( resource.getId() );
            List<Role> list = resource.getRoles();
            if ( list != null ) {
                user.setRoles( new HashSet<Role>( list ) );
            }
            else {
                user.setRoles( new HashSet<Role>() );
            }
            user.setTitle( resource.getTitle() );
            user.setLastName( resource.getLastName() );
            user.setFirstName( resource.getFirstName() );
            user.setInviteName( resource.getInviteName() );
            user.setPhoneNumber( resource.getPhoneNumber() );
            user.setEmail( resource.getEmail() );
            user.setUid( resource.getUid() );
            user.setStatus( resource.getStatus() );
            user.setProfileId( resource.getProfileId() );
            Set<Long> set = resource.getTermsAndConditionsIds();
            if ( set != null ) {
                user.setTermsAndConditionsIds( new HashSet<Long>( set ) );
            }
            else {
                user.setTermsAndConditionsIds( new HashSet<Long>() );
            }
            user.setAllowMarketingEmails( resource.isAllowMarketingEmails() );
        }

        return user;
    }

    @Override
    public UserResource mapToResource(User domain) {

        UserResource userResource = new UserResource();

        if ( domain != null ) {
            String name = domainCreatedByName( domain );
            if ( name != null ) {
                userResource.setCreatedBy( name );
            }
            String name1 = domainModifiedByName( domain );
            if ( name1 != null ) {
                userResource.setModifiedBy( name1 );
            }
            Set<Role> set = domain.getRoles();
            if ( set != null ) {
                userResource.setRoles( new ArrayList<Role>( set ) );
            }
            else {
                userResource.setRoles( new ArrayList<Role>() );
            }
            userResource.setId( domain.getId() );
            userResource.setUid( domain.getUid() );
            userResource.setTitle( domain.getTitle() );
            userResource.setFirstName( domain.getFirstName() );
            userResource.setLastName( domain.getLastName() );
            userResource.setInviteName( domain.getInviteName() );
            userResource.setPhoneNumber( domain.getPhoneNumber() );
            userResource.setImageUrl( domain.getImageUrl() );
            userResource.setEmail( domain.getEmail() );
            userResource.setStatus( domain.getStatus() );
            userResource.setProfileId( domain.getProfileId() );
            userResource.setAllowMarketingEmails( domain.isAllowMarketingEmails() );
            Set<Long> set1 = domain.getTermsAndConditionsIds();
            if ( set1 != null ) {
                userResource.setTermsAndConditionsIds( new HashSet<Long>( set1 ) );
            }
            else {
                userResource.setTermsAndConditionsIds( new HashSet<Long>() );
            }
            userResource.setCreatedOn( domain.getCreatedOn() );
            userResource.setModifiedOn( domain.getModifiedOn() );
            userResource.setEdiStatus( domain.getEdiStatus() );
            userResource.setEdiReviewDate( domain.getEdiReviewDate() );
        }

        return userResource;
    }

    private String domainCreatedByName(User user) {
        if ( user == null ) {
            return null;
        }
        User createdBy = user.getCreatedBy();
        if ( createdBy == null ) {
            return null;
        }
        String name = createdBy.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String domainModifiedByName(User user) {
        if ( user == null ) {
            return null;
        }
        User modifiedBy = user.getModifiedBy();
        if ( modifiedBy == null ) {
            return null;
        }
        String name = modifiedBy.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
