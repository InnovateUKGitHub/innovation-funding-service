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
    stop
    docker-compose up -d --build

}
redeploy() {
    docker-compose exec $1 gradle cleanDeploy --daemon
}

noTest() {
     docker-compose exec $1 gradle cleanDeploy -x test --daemon
}

migrate() {
    docker-compose  exec data gradle flywayClean flywayMigrate --daemon
}

resetUsers() {
    cd setup-files/scripts/shibboleth
    ./reset-users.sh
    cd ../../../
}

init() {
    start
    noTest data
    noTest web
    migrate
    resetUsers
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
            rebuild
            ;;
        redeploy)
            redeploy $2
            ;;
        notest)
            noTest $2
            ;;
        init)
            init
            ;;
        migrate)
            migrate
            ;;
        resetUsers)
            resetUsers
            ;;
        clean)
            docker rmi -f $(docker images -q innovation*)
            docker rmi mysql:5.5.49
            docker rmi $(docker images -f "dangling=true" -q)
            docker volume ls -qf "dangling=true" | xargs docker volume rm
            ;;
        *)
            echo $"that will not work, please check the script to find out how to use it :D"
            exit 1
esac

cd $startingDir
