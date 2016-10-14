UPDATE `user` SET first_name = CONCAT('Firstname', id), last_name = CONCAT('Lastname', id)
WHERE first_name is NULL AND last_name IS NULL;