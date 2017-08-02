#!/bin/sh
mkdir -p data
for OBJECT in $(jq -c ".[]" $1)
do
  NAME=$(echo $OBJECT | jq ".name" | sed -e 's/[^A-Za-z0-9._-]//g')
  URL=$(echo $OBJECT | jq ".endpoint" | tr -d "\"")
  ab -c $2 -n $3 -e data/${NAME}.csv $URL
  sed 1d data/${NAME}.csv > data/${NAME}.csv2
  mv data/${NAME}.csv2 data/${NAME}.csv
done
