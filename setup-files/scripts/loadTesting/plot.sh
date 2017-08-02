#!/bin/sh

mkdir -p graphs
for URL in $(cat $1)
do
  echo "Plotting graph for: $URL"
  NAME=$(echo $URL | sed -e 's/[^A-Za-z0-9._-]/_/g')
  sed "s|NAME|$NAME|g
    s|URL|$URL|g" plot.gp > tmpfile
  gnuplot tmpfile
  rm tmpfile
done
