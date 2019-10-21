#!/bin/bash
## Expects a build directory next to the script. Will create one if it is not there.

usage() {
    echo "Usage: $0 [-pv] [IMAGE_NAME]"
    echo
    echo "Options:"
    echo " -p : Pull images before running scan"
    echo " -v : verbose output"
    exit 1
}

redirect_stderr() {
    if [ "$VERBOSE" = 1 ]; then
        "$@"
    else
        "$@" 2>/dev/null
    fi
}

log() {
    # Execute the command passed and use the final parameter (the docker image name) as part of the filename to send output to.
    IMAGE_NAME="${!#}"
    ESCAPED_IMAGE_NAME="${IMAGE_NAME//\//-}"
    "$@" | tee scan."$ESCAPED_IMAGE_NAME".log
}

redirect_all() {
    if [ "$VERBOSE" = 1 ]; then
        "$@"
    else
        "$@" 2>/dev/null >/dev/null
    fi
}

PULL=0
VERBOSE=0

while getopts ":phv" opt; do
    case $opt in
        p)
            PULL=1
            ;;
        v)
            VERBOSE=1
            ;;
        \?)
            echo "Invalid option: -$OPTARG" >&2
            usage
            ;;
        h)
            usage
            ;;
    esac
done
# Remove all option parameters now they have be read in
shift $(($OPTIND -1))

# cd into the directory with the script then cd into the build directory
cd $(cd -P -- "$(dirname -- "$0")" && pwd -P)
mkdir -p build
cd build

# Download docker if it is not present.

echo "qqRP"
echo pwd
echo ls -lrt

if [ ! -f "docker-compose" ]; then
    curl -L https://github.com/docker/compose/releases/download/1.22.0/docker-compose-$(uname -s)-$(uname -m) -o docker-compose
    chmod +x docker-compose
fi

# Download the clair container scan yaml
if [ ! -f "docker-compose.yaml" ]; then
    wget -q https://raw.githubusercontent.com/usr42/clair-container-scan/master/docker-compose.yaml
fi

[ "$PULL" = 1 ] && redirect_all docker-compose pull
redirect_stderr log docker-compose run --rm scanner "$@"
ret=$?
redirect_all docker-compose down
exit $ret
