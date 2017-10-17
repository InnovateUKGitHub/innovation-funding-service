# Oracle JRE image

## Introduction
This is a base image for a Oracle Server JRE
It is based on the most recent current Ubuntu LTS release [1],
and downloads the Server JRE from Oracle directly from [2].

It uses a multi-stage Dockerfile, to ensure that no install dependencies leak into the JRE container.

## Why a custom image?
By the Oracle licences, it is _not_ allowed for any other party than Oracle to re-distribute Oracle JRE's.
Therefore we should _not_ depend on any 3rd party base docker image to provide us with an Oracle JRE.

## How to update
 - To update the Ubuntu version, visit [1], and change the tag to the latest ubuntu LTS release.
 - To update the JDK release
   1. Update the environmental variables `JRE_VERSON` in the `Dockerfile`
   2. Update the environmental variable `JRE_DOWNLOAD_URL` (also in `Dockerfile`), find the link on [2]
   3. Download that file, use sha256sum to compute  the new value for `JRE_SHA256`
- To update the JCE policy file
  - Apply the same steps as for updating a JDK relase, but for the policy file ;)

## References
[1] Visit https://www.ubuntu.com/download/server, find current LTS
[2] http://www.oracle.com/technetwork/java/javase/downloads/server-jre8-downloads-2133154.html
