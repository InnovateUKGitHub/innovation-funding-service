#!/bin/sh
echo "testing endpoints.txt with concurrency 25 and 1000 requests"
 ./run-test.sh health-endpoints.json 25 1000 && ./plot.sh health-endpoints.json
