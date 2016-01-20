installed=$(docker images | grep 'g2g3/ifs-local-dev')

if [ -n "$installed" ]; then

  echo "Shibboleth already installed - not attempting to reinstall"
  exit 0

fi


./_install-latest-shibboleth-image.sh

echo "Shibboleth installed successfully"
