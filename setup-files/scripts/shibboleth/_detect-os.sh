if [[ $OSTYPE == linux* ]]; then
  os=linux
elif [[ $OSTYPE == darwin* ]]; then
  os=mac
else
  echo "Unable to determine a supported operating system for this script.  Currently only supported on Linux and Macs"
  exit 1
fi

echo ${os}
