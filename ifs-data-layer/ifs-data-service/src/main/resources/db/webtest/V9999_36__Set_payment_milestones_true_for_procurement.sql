UPDATE competition
SET include_payment_milestone = FALSE
WHERE funding_type <> 'PROCUREMENT';

UPDATE competition
SET include_payment_milestone = TRUE
WHERE funding_type = 'PROCUREMENT';