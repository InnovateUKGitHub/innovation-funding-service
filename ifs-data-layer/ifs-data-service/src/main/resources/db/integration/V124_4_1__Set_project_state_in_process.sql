--IFS-3683 - This script inserts the missing ProjectProcess entries for various projects

insert into process (event, last_modified, process_type, target_id, activity_state_id) values
('project-created', NOW(), 'ProjectProcess', 5, 17),
('project-created', NOW(), 'ProjectProcess', 3, 17),
('project-created', NOW(), 'ProjectProcess', 2, 17),
('project-created', NOW(), 'ProjectProcess', 6, 17),
('project-created', NOW(), 'ProjectProcess', 7, 17);