#/bin/bash

set -e

## TODO DW - remove debug flag
set -x

REPLACE_REPLACEMENT_TOKEN_EXTRACTOR="s/^REPLACE('\(.*\)')$/\1/g"

MASK_REPLACEMENT_INDEX_EXTRACTOR="s/^MASK(\([0-9]\+\),[ ]*'\(.*\)')$/\1/g"
MASK_REPLACEMENT_TOKEN_EXTRACTOR="s/^MASK(\([0-9]\+\),[ ]*'\(.*\)')$/\2/g"

INTEGER_REPLACEMENT_DEVIATION_EXTRACTOR="s/^INTEGER(\(.*\))$/\1/g"

DIFFERENT_IF_NUMBER_REPLACEMENT_DEVIATION_EXTRACTOR="s/^DIFFERENT_IF_NUMBER(\(.*\))$/\1/g"

DECIMAL_REPLACEMENT_DEVIATION_EXTRACTOR="s/^DECIMAL(\(.*\),[ ]*\(.*\))$/\1/g"
DECIMAL_REPLACEMENT_SCALE_EXTRACTOR="s/^DECIMAL(\(.*\),[ ]*\(.*\))$/\2/g"

EMAIL_MASK_TOKEN_EXTRACTOR="s/^EMAIL('\(.*\)')$/\1/g"

HASH_MASK_TOKEN_EXTRACTOR="s/^HASH('\(.*\)')$/\1/g"

function generate_number_rewrite_rule() {

    column_name=$1
    deviation_percent=$2
    scale=$3
    deviation_percent_times_two=$((deviation_percent * 2))

    echo "ROUND($column_name * (1 + ((RAND() * $deviation_percent_times_two) / 100) - ($deviation_percent / 100)), $scale)"
}


# This function is able to generate SQL rewrite statements for common anonymisation techniques e.g. from the
# rewrite rule "MASK(2, 'X')", this function will generate the SQL necessary to reproduce the masking of
# all characters past the 2nd character with 'X'.
#
# If the rewrite rule passed in is not recognised as a common anonymisation technique, the unchanged rewrite rule
# will just be returned as-is
function generate_rewrite_from_rule() {

    column_name="$1"
    replacement="$2"

    # this case generates the SQL from a rewrite rule like "REPLACE('X')"
    replace_test=$(echo "$replacement" | sed "$REPLACE_REPLACEMENT_TOKEN_EXTRACTOR")
    if [[ "$replace_test" != "$replacement" ]]; then
        mask_token=$(echo "$replacement" | sed "$REPLACE_REPLACEMENT_TOKEN_EXTRACTOR")
        mask_token_length=$(expr length "${mask_token}")
        echo "SUBSTR(REPEAT('$mask_token', ((CHAR_LENGTH($column_name) / $mask_token_length) + 1)), 1, CHAR_LENGTH($column_name))"
        exit 0
    fi

    # this case generates the SQL from a rewrite rule like "MASK(1, 'X')"
    replace_test=$(echo "$replacement" | sed "$MASK_REPLACEMENT_INDEX_EXTRACTOR")
    if [[ "$replace_test" != "$replacement" ]]; then
        mask_index=$(echo "$replacement" | sed "$MASK_REPLACEMENT_INDEX_EXTRACTOR")
        mask_token=$(echo "$replacement" | sed "$MASK_REPLACEMENT_TOKEN_EXTRACTOR")
        echo "CONCAT(SUBSTR($column_name, 1, $mask_index), REPEAT('$mask_token', CHAR_LENGTH($column_name) - $mask_index))"
        exit 0
    fi

    # this case generates the SQL from a rewrite rule like "INTEGER(0.5)"
    replace_test=$(echo "$replacement" | sed "$INTEGER_REPLACEMENT_DEVIATION_EXTRACTOR")
    if [[ "$replace_test" != "$replacement" ]]; then

        deviation_percent=$(echo "$replacement" | sed "$INTEGER_REPLACEMENT_DEVIATION_EXTRACTOR")
        echo "$(generate_number_rewrite_rule $column_name $deviation_percent 0)"
        exit 0
    fi

    # this case generates the SQL from a rewrite rule like "DIFFERENT_IF_NUMBER(10)"
    replace_test=$(echo "$replacement" | sed "$DIFFERENT_IF_NUMBER_REPLACEMENT_DEVIATION_EXTRACTOR")
    if [[ "$replace_test" != "$replacement" ]]; then

        deviation_percent=$(echo "$replacement" | sed "$DIFFERENT_IF_NUMBER_REPLACEMENT_DEVIATION_EXTRACTOR")
        echo "IF($column_name REGEXP '^[0-9.]+$', $(generate_number_rewrite_rule $column_name $deviation_percent 0), $column_name)"
        exit 0
    fi

    # this case generates the SQL from a rewrite rule like "DECIMAL(20, 2)"
    replace_test=$(echo "$replacement" | sed "$DECIMAL_REPLACEMENT_DEVIATION_EXTRACTOR")
    if [[ "$replace_test" != "$replacement" ]]; then

        deviation_percent=$(echo "$replacement" | sed "$DECIMAL_REPLACEMENT_DEVIATION_EXTRACTOR")
        scale=$(echo "$replacement" | sed "$DECIMAL_REPLACEMENT_SCALE_EXTRACTOR")
        echo "$(generate_number_rewrite_rule $column_name $deviation_percent $scale)"
        exit 0
    fi

    replace_test=$(echo "$replacement" | sed "$EMAIL_MASK_TOKEN_EXTRACTOR")
    if [[ "$replace_test" != "$replacement" ]]; then

        mask_token=$(echo "$replacement" | sed "$EMAIL_MASK_TOKEN_EXTRACTOR")
        echo "CONCAT(CONCAT(SUBSTR($column_name, 1, 2), REPEAT('x', INSTR($column_name, '@') - 2)), CONCAT(SUBSTR($column_name, INSTR($column_name, '@'), 3), 'xxxxx.com'))"
        exit 0
    fi

    replace_test=$(echo "$replacement" | sed "$HASH_MASK_TOKEN_EXTRACTOR")
    if [[ "$replace_test" != "$replacement" ]]; then

        mask_token=$(echo "$replacement" | sed "$HASH_MASK_TOKEN_EXTRACTOR")
        echo "CONCAT(SUBSTR($column_name, 1, 9), CONCAT(CONCAT(CONCAT(CONCAT(REPEAT('$mask_token', 4), '-'), CONCAT(REPEAT('$mask_token', 4), '-'))), CONCAT(REPEAT('$mask_token', 4), '-')), REPEAT('$mask_token', 12))"
        exit 0
    fi

    echo "$replacement"
}