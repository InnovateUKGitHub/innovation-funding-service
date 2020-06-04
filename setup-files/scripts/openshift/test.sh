#!/usr/bin/env bash

# Below command can be replaced to the required CLI, including with custom JSON output, assuming the NextToken is in the same location.
AWS_CLI_COMMAND="aws ssm get-parameters-by-path --path /CI/IFS/NON-NAMED/PROPERTIES/ --max-items 1 --query={NextToken:NextToken,Parameters[].Value}"
OUTPUT_FILE="./output-$(date +%s)"

function CLI_call() {
  if [ ! -v NEXT_TOKEN ]; then
    cli_output=$($AWS_CLI_COMMAND)
  else
    cli_output=$($AWS_CLI_COMMAND --next-token $NEXT_TOKEN)
  fi

  echo $cli_output >> $OUTPUT_FILE
  echo $cli_output | jq -r ".NextToken"
}

while [ "$NEXT_TOKEN" != "null" ]; do
  NEXT_TOKEN=$(CLI_call $NEXT_TOKEN)
done

# Not 100% necessary but can be used to cleanup the output from the multiple API calls into a neater JSON formatted output.
cat $OUTPUT_FILE | jq [.PlatformARNs[]] > $OUTPUT_FILE-master.json
rm $OUTPUT_FILE