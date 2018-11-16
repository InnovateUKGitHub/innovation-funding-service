#!/bin/sh

if [ $# -ne 8 ] ; then
  echo "\nExactly 8 parameters expected, you provided $#.
Usage: ws.sh <API_key> <Project_Token> <Product_Token> <Lib_Paths_as_CSV> <Extensions_as_CSV> <Resolve_all_dep> <Force_check_all_dep> <Run_pre_step>\n"
  exit 1
fi

APIKEY=$1
PROJECT_TOKEN=$2
PRODUCT_TOKEN=$3
LIB_PATHS=$4
INCL_EXT=$5
RESOLVE_ALL=$6
FORCE_CHECK_ALL=$7
RUN_PRE_STEP=$8

# Parse 5th param (e.g. "js,jar,tar.gz") and 
# format for 'includes' string (e.g. "**/*.js **/*.jar **/*.tar.gz ")
for e in $(echo ${INCL_EXT} | sed "s/,/ /g") ; do
  EXTENSIONS="${EXTENSIONS}**/*.${e} "
done

curl -sLJO https://github.com/whitesource/fs-agent-distribution/raw/master/standAlone/whitesource-fs-agent.jar
mv whitesource-fs-agent.jar whitesource/whitesource-fs-agent.jar

# add gradle options down here to the config
cat <<EOF >whitesource/whitesource-fs-agent.config
apiKey=${APIKEY}
projectToken=${PROJECT_TOKEN}
productToken=${PRODUCT_TOKEN}
forceUpdate=true
forceUpdate.failBuildOnPolicyViolation=true
checkPolicies=true
forceCheckAllDependencies=${FORCE_CHECK_ALL}
offline=false
showProgressBar=false
includes=${EXTENSIONS}
excludes=**/*.sources.jar **/*javadoc.jar **/whitesource/**
npm.resolveDependencies=${RESOLVE_ALL}
npm.runPreStep=${RUN_PRE_STEP}
gradle.resolveDependencies=${RESOLVE_ALL}
gradle.runPreStep=${RUN_PRE_STEP}
gradle.aggregateModules=true
archiveExtractionDepth=1
EOF

java -jar whitesource/whitesource-fs-agent.jar -c whitesource/whitesource-fs-agent.config -d ${LIB_PATHS}
EXIT_CODE=$(expr $? % 256)
rm whitesource/whitesource-fs-agent.jar
exit ${EXIT_CODE}
