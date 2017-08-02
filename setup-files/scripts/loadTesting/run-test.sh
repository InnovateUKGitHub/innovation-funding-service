#!/bin/sh
mkdir -p data
for URL in $(cat $1)
do
  NAME=$(echo $URL | sed -e 's/[^A-Za-z0-9._-]/_/g')
  ab -c $2 -n $3 -g data/${NAME}.tsv $URL
done
