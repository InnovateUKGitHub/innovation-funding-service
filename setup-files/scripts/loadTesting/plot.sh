#!/bin/sh

mkdir -p graphs
for NAME in $(jq -c ".[].name" $1 | sed -e 's/[^A-Za-z0-9._-]//g')
do
  echo "Plotting graph for: $NAME"
  sed "s|NAME|$NAME|g" plot.gp > tmpfile
  gnuplot tmpfile
  rm tmpfile
done
