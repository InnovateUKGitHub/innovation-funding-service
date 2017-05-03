#!/usr/bin/env bash

# This script is called by a Coscale agent to return metrics about the service.
# It is initially called with the '-c' argument to return the configuration.
# It is then regularly polled with the '-d' argument which returns the data metrics defined by the config.

for i in "$@"
do
    case $i in

        -c)
            # Configuration mode: return the definition of the custom metrics (JSON format)

            echo -n '{';
            # First we define how long the script is allowed to run
            echo -n '"maxruntime":1000,'

            # Second we set the period the script will run, in this case it will run every 60 seconds
            echo -n '"period": 60,'

            # Next we define the metrics we want the CoScale agent to fetch every minute
            echo -n '"metrics":['
            echo -n '    {'
            echo -n '        "id": 1,'
            echo -n '        "datatype": "DOUBLE",'
            echo -n '        "name": "AJP Connection Count",'
            echo -n '        "description": "Number of 8009 (AJP) connections",'
            echo -n '        "groups": "Network",'
            echo -n '        "unit": "",'
            echo -n '        "tags": "",'
            echo -n '        "calctype": "Instant"'
            echo -n '    }'
            echo    ']}'
            ;;

        -d)
            # Data retrieval mode: return the data for the custom metrics (format is defined by the config above)

            # Get the number of connections to port 8009
            echo "M1 $(netstat -t -n | awk '{print $4}' | grep ':8009' | wc -l)"
            ;;
    esac
done
