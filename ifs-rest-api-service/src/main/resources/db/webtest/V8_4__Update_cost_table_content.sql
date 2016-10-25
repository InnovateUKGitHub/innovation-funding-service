-- Make sure rows made in the past, conform with our new columns / default values.

UPDATE cost SET name = "working-days-per-year" where description = "Working days per year" and question_id = 28;
UPDATE cost SET name = "grant-claim" where description = "Grant Claim" and question_id = 38;
UPDATE cost SET name = "other-funding" where description = "Other Funding" and question_id = 35;
UPDATE cost SET name = "overhead" where description = "Accept Rate" and question_id = 29;