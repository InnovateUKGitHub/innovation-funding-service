-- IFS-5098 add vat for procurement competitions

INSERT INTO finance_row (`cost`, `description`, `item`, `quantity`, `name`, `target_id`, `row_type`, `type`)
SELECT
'0' AS cost,
'VAT' AS cost,
'false' AS cost,
'0' AS cost,
'vat' AS cost,
target_id AS target_id,
'ApplicationFinanceRow' AS row_type,
'VAT' AS type
 FROM finance_row GROUP BY target_id;