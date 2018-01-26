#!/bin/bash
# Series of functions to help spin up individual pods

# Function to delete any pre-existing pods before starting a new one.
function startupPod() {
    POD_CONFIG_PATH=$1
    SVC_ACCOUNT_CLAUSE=$2
    if [ -z "${POD_CONFIG_PATH}" ]; then echo "POD_CONFIG_PATH variable not set"; exit -1; fi
    if [ -z "${SVC_ACCOUNT_CLAUSE}" ]; then echo "SVC_ACCOUNT_CLAUSE variable not set"; exit -1; fi

    echo "Starting up a new pod using configuration file ${POD_CONFIG_PATH}"
    until oc create -f $(getBuildLocation)${POD_CONFIG_PATH} ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
    do
      echo "Shutting down any pre-existing pods before starting a new one."
      oc delete -f $(getBuildLocation)${POD_CONFIG_PATH} ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
      sleep 10
    done
    echo "Pod started using configuration file ${POD_CONFIG_PATH}"
}

# Function to wait until the pod reports a Running status.
# This does not necessarily indicate that any processes in the pod are at a point to use.
# If this is required it may be necessary to wait for output in the pods log. See waitForPodLogs.
function waitForPodToStart() {
    POD_NAME=$1
    SVC_ACCOUNT_CLAUSE=$2
    if [ -z "${POD_NAME}" ]; then echo "POD_NAME variable not set"; exit -1; fi
    if [ -z "${SVC_ACCOUNT_CLAUSE}" ]; then echo "SVC_ACCOUNT_CLAUSE variable not set"; exit -1; fi

    until oc get pods ${POD_NAME} ${SVC_ACCOUNT_CLAUSE} | grep "Running" &> /dev/null;
    do
      echo "Wait for pod ${POD_NAME} to start running"
      sleep 2
    done
    echo "Pod ${POD_NAME} running"
}

# Function to wait until the provided text is seen in pod logs.
function waitForPodLogs() {
    POD_NAME=$1
    POD_LOG_TEXT=$2
    SVC_ACCOUNT_CLAUSE=$3
    if [ -z "${POD_NAME}" ]; then echo "POD_NAME variable not set"; exit -1; fi
    if [ -z "${POD_LOG_TEXT}" ]; then echo "POD_LOG_TEXT variable not set"; exit -1; fi
    if [ -z "${SVC_ACCOUNT_CLAUSE}" ]; then echo "SVC_ACCOUNT_CLAUSE variable not set"; exit -1; fi

    echo "Waiting until the text ${POD_LOG_TEXT} is seen in the pod ${POD_NAME} logs"
    until oc logs ${POD_NAME} ${SVC_ACCOUNT_CLAUSE} | grep "${POD_LOG_TEXT}" &> /dev/null;
    do
      echo "Waiting to see text ${POD_LOG_TEXT} in pod ${POD_NAME} logs"
      sleep 2
    done
    echo "Text ${POD_LOG_TEXT} seen in the pod ${POD_NAME} logs"
}

# Function to shutdown then delete a pod
function deletePod() {
    POD_NAME=$1
    POD_CONFIG_PATH=$2
    SVC_ACCOUNT_CLAUSE=$3
    if [ -z "${POD_NAME}" ]; then echo "POD_NAME variable not set"; exit -1; fi
    if [ -z "${POD_CONFIG_PATH}" ]; then echo "POD_CONFIG_PATH variable not set"; exit -1; fi
    if [ -z "${SVC_ACCOUNT_CLAUSE}" ]; then echo "SVC_ACCOUNT_CLAUSE variable not set"; exit -1; fi

    echo "Deleting pod ${POD_NAME} with configuration file."
    oc delete -f $(getBuildLocation)${POD_CONFIG_PATH} ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
    max_termination_timeout_seconds=$((120))
    time_waited_so_far=$((0))

    until ! oc get pod ${POD_NAME} ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
    do
      if [ "${time_waited_so_far}" -gt "${max_termination_timeout_seconds}" ]; then
        echo "${POD_NAME} pod didn't shut down as expected"
        exit -1;
      fi
      echo "Still waiting for ${POD_NAME} pod to shut down..."
      sleep 2
      time_waited_so_far=$((time_waited_so_far + 5))
    done
    echo "Deleted pod ${POD_NAME}"
}
