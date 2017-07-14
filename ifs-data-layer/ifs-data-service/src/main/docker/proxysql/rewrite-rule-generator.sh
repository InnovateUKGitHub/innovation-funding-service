#/bin/bash

#
# This script takes the rewrite rules defined in the various csv files and converts them to SQL statements.
#
# An example would be for an entry in a csv file like: description|REPLACE('xxyy')
# This script would replace it by SQL like: SUBSTR(REPEAT('xxyy', ((CHAR_LENGTH(description) / 4) + 1)), 1, CHAR_LENGTH(description))
#
# Supported replacements:
#
# REPLACE('blah ') - replaces a column's contents with the given string 'blah ', repeating the string like 'blah blah blah bl...'
#                    until the original length of the column contents is reached
#
#                    e.g. "Hello there!" would be replaced by "blah blah bl"
#
# MASK(2, 'x') - retains the first 2 character of a column, but replaces the rest with 'x'
#
#                e.g. "Hello there!" would be replaced by "Hexxxxxxxxxx"
#
# INTEGER(10) - replaces the given column's integer with another integer that can deviat 10% up or 10% down from the original value
#
#               e.g. 10 could be replaced by 9, 10 or 11
#
# DIFFERENT_IF_NUMBER(10) - replaces the given column's number with another number that can deviate 10% up or 10% down from the
#                           original value but ONLY if it is a numerical value
#
#                           e.g. 10 could be replaced by 9, 10, or 11, but "Hello" will remain as "Hello"
#
# DECIMAL(10, 2) - replaces the given column's decimal value with another thqat can deviate 10% up or down from the original value, and
#                  keep the scale of the decimal at 2 decimal places
#
#                  e.g. 10.10 could be replaced by anything from 8.95 to 11.05, always retaining 2 decimal places
#
# EMAIL('x') - replaces a given column's email value with a masked email value that retains the first 2 characters of the start string
#              and replaces the rest with 'x' up to the '@' symbol.  Thereafter the @ portion would be replaced with 'xx.example.com'
#
#              e.g. alison@uni.com would be replaced by alxxxx@xx.example.com
#
# UUID('x') - replaces a UUID with 'x' symbols apat from the first 8 characters and hyphens
#
#             e.g. "12345678-1234-5678-2345-123456789012" would be replaced by "12345678-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
#
set -e

REPLACE_REPLACEMENT_TOKEN_EXTRACTOR="s/^REPLACE('\(.*\)')$/\1/g"

MASK_REPLACEMENT_INDEX_EXTRACTOR="s/^MASK(\([0-9]\+\),[ ]*'\(.*\)')$/\1/g"
MASK_REPLACEMENT_TOKEN_EXTRACTOR="s/^MASK(\([0-9]\+\),[ ]*'\(.*\)')$/\2/g"

INTEGER_REPLACEMENT_DEVIATION_EXTRACTOR="s/^INTEGER(\(.*\))$/\1/g"

DIFFERENT_IF_NUMBER_REPLACEMENT_DEVIATION_EXTRACTOR="s/^DIFFERENT_IF_NUMBER(\(.*\))$/\1/g"

DECIMAL_REPLACEMENT_DEVIATION_EXTRACTOR="s/^DECIMAL(\(.*\),[ ]*\(.*\))$/\1/g"
DECIMAL_REPLACEMENT_SCALE_EXTRACTOR="s/^DECIMAL(\(.*\),[ ]*\(.*\))$/\2/g"

EMAIL_MASK_TOKEN_EXTRACTOR="s/^EMAIL('\(.*\)')$/\1/g"

UUID_MASK_TOKEN_EXTRACTOR="s/^UUID('\(.*\)')$/\1/g"

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

    # this case generates the SQL from a rewrite rule like "EMAIL('x')"
    replace_test=$(echo "$replacement" | sed "$EMAIL_MASK_TOKEN_EXTRACTOR")
    if [[ "$replace_test" != "$replacement" ]]; then

        mask_token=$(echo "$replacement" | sed "$EMAIL_MASK_TOKEN_EXTRACTOR")
        echo "CONCAT(CONCAT(SUBSTR($column_name, 1, 2), REPEAT('$mask_token', INSTR($column_name, '@') - 2)), CONCAT(SUBSTR($column_name, INSTR($column_name, '@'), 3), '$mask_token$mask_token.example.com'))"
        exit 0
    fi

    # this case generates the SQL from a rewrite rule like "UUID('x')"
    replace_test=$(echo "$replacement" | sed "$UUID_MASK_TOKEN_EXTRACTOR")
    if [[ "$replace_test" != "$replacement" ]]; then

        mask_token=$(echo "$replacement" | sed "$UUID_MASK_TOKEN_EXTRACTOR")
        echo "CONCAT(SUBSTR($column_name, 1, 9), CONCAT(CONCAT(CONCAT(CONCAT(REPEAT('$mask_token', 4), '-'), CONCAT(REPEAT('$mask_token', 4), '-'))), CONCAT(REPEAT('$mask_token', 4), '-')), REPEAT('$mask_token', 12))"
        exit 0
    fi

    echo "$replacement"
}