package org.innovateuk.ifs.user.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.innovateuk.ifs.user.domain.Affiliation;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-09-29T08:10:06+0100",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 11.0.15 (Eclipse Adoptium)"
)
@Component
public class AffiliationMapperImpl extends AffiliationMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public AffiliationResource mapToResource(Affiliation domain) {

        AffiliationResource affiliationResource = new AffiliationResource();

        if ( domain != null ) {
            affiliationResource.setId( domain.getId() );
            affiliationResource.setUser( userMapper.mapUserToId( domain.getUser() ) );
            affiliationResource.setAffiliationType( domain.getAffiliationType() );
            affiliationResource.setExists( domain.getExists() );
            affiliationResource.setRelation( domain.getRelation() );
            affiliationResource.setOrganisation( domain.getOrganisation() );
            affiliationResource.setPosition( domain.getPosition() );
            affiliationResource.setDescription( domain.getDescription() );
        }

        return affiliationResource;
    }

    @Override
    public List<AffiliationResource> mapToResource(Iterable<Affiliation> domain) {
        if ( domain == null ) {
            return new ArrayList<AffiliationResource>();
        }

        List<AffiliationResource> list = new ArrayList<AffiliationResource>();
        for ( Affiliation affiliation : domain ) {
            list.add( mapToResource( affiliation ) );
        }

        return list;
    }

    @Override
    public Affiliation mapToDomain(AffiliationResource resource) {

        Affiliation affiliation = new Affiliation();

        if ( resource != null ) {
            affiliation.setId( resource.getId() );
            affiliation.setUser( userMapper.mapIdToDomain( resource.getUser() ) );
            affiliation.setAffiliationType( resource.getAffiliationType() );
            affiliation.setExists( resource.getExists() );
            affiliation.setRelation( resource.getRelation() );
            affiliation.setOrganisation( resource.getOrganisation() );
            affiliation.setPosition( resource.getPosition() );
            affiliation.setDescription( resource.getDescription() );
        }

        return affiliation;
    }

    @Override
    public Iterable<Affiliation> mapToDomain(Iterable<AffiliationResource> resource) {
        if ( resource == null ) {
            return new ArrayList<Affiliation>();
        }

        ArrayList<Affiliation> iterable = new ArrayList<Affiliation>();
        for ( AffiliationResource affiliationResource : resource ) {
            iterable.add( mapToDomain( affiliationResource ) );
        }

        return iterable;
    }
}
