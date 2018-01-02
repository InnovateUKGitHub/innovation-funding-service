/*
- Purpose of this patch is to allow setting uuid for system user (that is used for anonymoys operations such as viewing public content or registration).
- This is repeatable patch.  It will be applied every time checksum of this file changes.  Repeatable patches are always executed after versioned patches.
- system.user.uuid is defined in docker-build.gradle and is environment specific.  This will be set to different value for demo and production
(or any environment with real/demo/non-webtest data)
- Append a date below each time UUID change needs to be applied to any enviornment.  This is required as repeatable patch will only run if checksum changes:
  02/01/2018
*/
UPDATE `user` SET `uid`='${ifs.system.user.uuid}' WHERE `email`='ifs_web_user@innovateuk.org';