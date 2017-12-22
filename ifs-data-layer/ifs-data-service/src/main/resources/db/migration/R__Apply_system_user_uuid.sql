/*
- Purpose of this patch is to allow setting uuid for system user (that is used for anonymoys operations such as viewing public content or registration).
- This is repeatable patch.  It will be applied every time checksum of this file changes.  Repeatable patches are always executed after versioned patches.
- system.user.uuid is defined in docker-build.gradle and is environment specific.  This will be set to different value for demo and production
(or any environment with real/demo/non-webtest data) */
UPDATE `user` SET `uid`='_${ifs.system.user.uuid}' WHERE `email`='ifs_web_user@innovateuk.org';