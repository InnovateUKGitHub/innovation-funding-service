# Shibboleth REST API #

An API for maintaining identity records for an identity provider for the purpose of offering single sign-on to supported service providers.

## Basic API Action Overview ##

### Create identity ###
* POST /identities

### Get identity ###
* GET /identities/{uid}

### Remove identity ###
* DELETE /identities/{uid}

### Update identity email ###
* PUT /identities/{uid}/email

### Update identity password ###
* PUT /identities/{uid}/password

## Detailed API Action Overview - Swagger Specification ##
To view the swagger specification you'll need the latest [swagger.xml](swagger.xml). A resource you may find useful if the Swagger Editor [http://editor.swagger.io](http://editor.swagger.io) which can be used to view and edit the specification but be mindful that it can still be buggy.