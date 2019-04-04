-- IFS-4208 - Update user table to use an enum for status, and include the new state of pending

ALTER TABLE `user` MODIFY `status` ENUM('ACTIVE', 'PENDING', 'INACTIVE');