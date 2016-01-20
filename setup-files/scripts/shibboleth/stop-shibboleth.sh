docker_id=$(./_get-shibboleth-instance-id.sh)

if [ -z "$docker_id" ]; then

  echo "Shibboleth isn't currently running"
  exit 1

fi

docker stop $docker_id
