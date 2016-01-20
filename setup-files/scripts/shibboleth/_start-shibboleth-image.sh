image_id=$(docker images | grep g2g3/ifs-local-dev | awk '{print $3}')

if [ -z "$image_id" ]; then

  echo "No Shibboleth image installed with Docker - try running ./start-shibboleth.sh"
  exit 1

fi

docker start $image_id
