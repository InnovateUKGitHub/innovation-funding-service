-- IFS-6480 add index to invites
ALTER TABLE invite ADD index target_id_type (target_id, type);