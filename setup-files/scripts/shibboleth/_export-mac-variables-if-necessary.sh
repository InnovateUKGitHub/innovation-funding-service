if [[ "$(./_detect-os.sh)" == "mac" ]]; then

  cd run/mac
  source _mac-set-docker-vars.sh
  cd ../..
fi
