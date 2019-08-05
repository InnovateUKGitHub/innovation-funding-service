-- IFS-4551
DELETE fir FROM form_input_response AS fir
INNER JOIN form_input fi ON fi.id = fir.form_input_id
WHERE fi.form_input_type_id = 6;