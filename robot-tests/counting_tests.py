#!/usr/bin/python

import subprocess
import re

# Implement bash commands inside python
def shell(command):
  process = subprocess.Popen(command.split(), stdout=subprocess.PIPE)
  output = process.communicate()[0]
  return output

# Clean up pybot's output to give a simple number of running tests
def tidyUpPybotOutput(pybot_output):
  for match in re.finditer("(\d*) tests", pybot_output):
    pass
  return match.group()




print "Counting the local running tests..."

local_running_tests_pybot_output = shell("pybot --outputdir counting_tests --pythonpath IFS_acceptance_tests/libs --dryrun --exclude Pending --exclude Failing --exclude PendingForLocal --exclude FailingForLocal IFS_acceptance_tests/tests")

local_running_tests = tidyUpPybotOutput(local_running_tests_pybot_output)


print "Counting the tests running on the dev server..."

dev_running_tests_pybot_output = shell("pybot --outputdir counting_tests --pythonpath IFS_acceptance_tests/libs --dryrun --exclude Pending --exclude Failing --exclude PendingForDev --exclude FailingForDev IFS_acceptance_tests/tests")

dev_running_tests = tidyUpPybotOutput(dev_running_tests_pybot_output)


print "Counting tagged tests..."

failing_tests_pybot_output = shell("pybot --outputdir counting_tests --pythonpath IFS_acceptance_tests/libs --dryrun --include Failing IFS_acceptance_tests/tests")

failing_tests = tidyUpPybotOutput(failing_tests_pybot_output)

pending_tests_pybot_output = shell("pybot --outputdir counting_tests --pythonpath IFS_acceptance_tests/libs --dryrun --include Pending IFS_acceptance_tests/tests")

pending_tests = tidyUpPybotOutput(pending_tests_pybot_output)


print "Counting total tests..."

total_tests_pybot_output = shell("pybot --outputdir counting_tests --pythonpath IFS_acceptance_tests/libs --dryrun IFS_acceptance_tests/tests")

total_tests = tidyUpPybotOutput(total_tests_pybot_output)



print ""

print  "Test report:"

print "Tests running locally:", str(local_running_tests)

print "Tests running against the dev server:", str(dev_running_tests)

print "Tests marked as Failing:", str(failing_tests)

print "Tests marked as Pending:", str(pending_tests)

print "Grand test total:", str(total_tests)





