#!/bin/bash

startingDir= pwd

case $OSTYPE in
    darwin*)
        echo "Mac detected"
        eval $(docker-machine env default)
        ;;
    linux*)
        echo "Linux detected"
        ;;
    *)
        echo "Unable to determine a supported operating system for this script.  Currently only supported on Linux and Macs"
        exit 1
        ;;
esac

cd ../../../

docker-compose stop