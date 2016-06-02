#!/bin/sh

startingDir= pwd

if [[ $OSTYPE == darwin* ]]; then
  eval $(docker-machine env default)
else
  echo "Unable to determine a supported operating system for this script.  Currently only supported on Linux and Macs"
  exit 1
fi


cd ../../../

start() {
    docker-compose up -d
}

stop() {
    docker-compose stop
}

restart() {
    docker-compose restart
}

rebuild() {
    docker-compose stop
    docker-compose up -d --build

}
redeploy() {
    docker-compose exec $1 gradle cleanDeploy --daemon
}

noTest() {
     docker-compose exec $1 gradle cleanDeploy -x test --daemon
}


case "$1" in
        start)
            start
            ;;
        stop)
            stop
            ;;
        restart)
            restart
            ;;
        rebuild)
            rebuild $2
            ;;
        redeploy)
            redeploy $2
            ;;
        notest)
            noTest $2
            ;;

        *)
            echo $"that will not work, please check the script to find out how to use it :D"
            exit 1
esac

cd $startingDir