#!/bin/sh

#
# This file contains shell functions that assist with the generation of shibboleth configuration files
# such as IDP/SP entity descriptor XML files with place holder replacement.
#

#
# Read the given certificate file into a variable
#
readCertificateFile() {
  sed '/^-----/d' $1 | sed '{:q;N;s/\n/\\n/g;t q}'
}

#
# Replace place holders in the file with the given place holder value, either from environment variable or load from
# configuration file.
#
replacePlaceholders() {
  file=$1
  shift
  #
  # Loop through all place holders and replace the value in the file
  #
  for name in "$@" ; do
    rows=`grep ${name} ${file} | wc -l`
    if [ ${rows} -eq 0 ] ; then
      echo "${file} : placeholder ignore - ${name} - nothing to replace"
    else
      echo "${file} : placeholder replace - ${name} - replacing ${rows} values"
      value=`eval echo \\$$name`
      if [ -z "$value" ] ; then
        name_FILE="${name}_FILE"
        variable_file=`eval echo \\$$name_FILE`
        if [ -z ${variable_file} ] ; then
          value="please-set-$name"
        else
          # Prepend default config directory if filename is relative name
          if [ "${variable_file%"${variable_file#?}"}" != "/" ] ; then
            variable_file="${LOCAL_CONFIG_DIR}/${variable_file}"
          fi
          echo "file containing variable ${name} : ${variable_file}"

          #
          # If environmental variable not provided and we have a file name defined, then read the file into this
          # variable.
          #
          if [ -f ${variable_file} ] ; then
            value=$(readCertificateFile ${variable_file})
          else
            value="please set ${name} or provide ${variable_file}"
          fi
        fi
      fi
      #
      # Special case, if value is set to __EMPTY__ then value is the empty string
      #
      if [ "${value}" = "__EMPTY__" ] ; then
        value=""
      fi
      echo ${value}
      sed -i "s#\${$name}#${value}#g" ${file}
    fi
  done
}

#
# Create string that can be appended after domain to specific port (if it's not default port)
#
createPortPostfix() {
  if [ "$2" = "https" -a "$1" != "443" ] ; then
    echo ":${1}"
  elif [ "$2" = "http" -a "$1" != "80" ] ; then
    echo ":${1}"
  else
    echo "__EMPTY__"
  fi
}

#
# Extract property from file
#
property() {
    grep "^${1}=" ${2} | cut -d'=' -f2
}

#
# Create entity descriptor XML files from properties files
#
createEntitiesFromProperties() {
  echo "Creating entities from properties"
  for properties in ${LOCAL_CONFIG_DIR}/entities/*.properties ; do
    DOMAIN=$(property DOMAIN ${properties})
    PORT=$(property PORT ${properties})
    PROTOCOL=$(property PROTOCOL ${properties})
    CERTIFICATE_FILE=$(property CERTIFICATE_FILE ${properties})
    CERTIFICATE_SUBJECT_NAME=$(property CERTIFICATE_SUBJECT_NAME ${properties})
    ENCRYPTION_CERTIFICATE_FILE=$(property ENCRYPTION_CERTIFICATE_FILE ${properties})

    : "${DOMAIN:=localhost}"
    : "${PORT:=443}"
    : "${PROTOCOL:=https}"
    : "${CERTIFICATE_SUBJECT_NAME:=CN=localhost,O=organisation,L=location,ST=state,C=GB}"
    PORT_POSTFIX=$(createPortPostfix ${PORT} ${PROTOCOL})
    TEMPLATE=$(property TEMPLATE ${properties})

    if [ "${PORT_POSTFIX}" = "__EMPTY__" ] ; then
      entityName="${PROTOCOL}://${DOMAIN}"
    else
      entityName="${PROTOCOL}://${DOMAIN}${PORT_POSTFIX}"
    fi
    entityNameHash=`echo -n ${entityName} | openssl sha1 | sed 's/^.* //'`
    echo "Creating entity ${entityName} using template ${TEMPLATE} : ${entityNameHash}"

    entityFileName=${SHIBBOLETH_CONF_DIR}/metadata/${entityNameHash}.xml
    templateFileName=${LOCAL_CONFIG_DIR}/templates/${TEMPLATE}
    cp ${templateFileName} ${entityFileName}

    replacePlaceholders ${entityFileName} DOMAIN PROTOCOL PORT_POSTFIX CERTIFICATE ENCRYPTION_CERTIFICATE
  done
}