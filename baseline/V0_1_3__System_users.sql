INSERT INTO `user` (email, first_name, last_name, status, uid, system_user) 
     VALUES ('ifs_system_maintenance_user@innovateuk.org','IFS','System Maintenance User','ACTIVE','88c9c1ba-645f-4a85-95dc-c0bb165caac2',1);
INSERT INTO `user` (email, first_name, last_name, status, uid, system_user) 
     VALUES ('ifs_web_user@innovateuk.org', 'IFS Web', 'System User', 'ACTIVE', '8394d970-b250-4b15-9621-3534325691b4', 1);

INSERT INTO user_role (user_id, role_id)
  SELECT u.id, r.id 
    FROM `user` u, `role` r 
   WHERE u.email = 'ifs_system_maintenance_user@innovateuk.org' 
     AND r.name = 'system_maintainer';
INSERT INTO user_role (user_id, role_id)
  SELECT u.id, r.id 
    FROM `user` u, `role` r 
   WHERE u.email = 'ifs_web_user@innovateuk.org' 
     AND r.name = 'system_registrar';
