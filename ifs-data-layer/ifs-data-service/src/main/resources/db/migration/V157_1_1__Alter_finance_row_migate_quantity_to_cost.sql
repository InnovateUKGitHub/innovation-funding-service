-- IFS-6696 - Updating the finance row and migrate quantity data to cost.
UPDATE finance_row SET cost = quantity WHERE name = "grant-claim" AND type = "FINANCE";
UPDATE finance_row SET quantity = null WHERE name = "grant-claim" AND type = "FINANCE";