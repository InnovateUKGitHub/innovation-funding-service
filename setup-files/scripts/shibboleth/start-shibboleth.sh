set -e

shibboleth_id=$(./_get-shibboleth-instance-id.sh)

if [ -n "$shibboleth_id" ]; then
  
  echo "Shibboleth is already running."
  exit 1

fi

./_install-shibboleth-if-necessary.sh

echo ""
echo "Starting Shibboleth..."
echo ""
./_run-shibboleth-image.sh

echo ""
echo "Shibboleth started successfully"
