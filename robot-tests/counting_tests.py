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
  for match in re.finditer("\d* tests", pybot_output):
    pass
  if match:
    return match.group()
  else:
    for single_match in re.finditer("\d test", pybot_output):
      pass
    if single_match:
      return single_match.group()
    else:
      return "0 tests!"

# Clean up grep's output to give a tidy directory for each failing test
def tidyUpFailingTestSearch(grep_output):
  list_of_failing_test_locations = []
  for match in re.findall("(\S*)\:", grep_output):
    list_of_failing_test_locations.append(match)
  return list_of_failing_test_locations



print "Counting the local running tests..."

local_running_tests_pybot_output = shell("pybot --outputdir test_counting --pythonpath IFS_acceptance_tests/libs --dryrun --exclude Pending --exclude Failing --exclude PendingForLocal --exclude FailingForLocal IFS_acceptance_tests/tests")

local_running_tests = tidyUpPybotOutput(local_running_tests_pybot_output)


print "Counting the tests running on the dev server..."

dev_running_tests_pybot_output = shell("pybot --outputdir test_counting --pythonpath IFS_acceptance_tests/libs --dryrun --exclude Pending --exclude Failing --exclude PendingForDev --exclude FailingForDev IFS_acceptance_tests/tests")

dev_running_tests = tidyUpPybotOutput(dev_running_tests_pybot_output)


print "Counting tagged tests..."

failing_tests_pybot_output = shell("pybot --outputdir test_counting --pythonpath IFS_acceptance_tests/libs --dryrun --include Failing IFS_acceptance_tests/tests")

failing_tests = tidyUpPybotOutput(failing_tests_pybot_output)

if failing_tests != "0 tests":
  failing_test_list = shell("grep -R Failing IFS_acceptance_tests/")

tidy_failing_test_list = tidyUpFailingTestSearch(failing_test_list)

pending_tests_pybot_output = shell("pybot --outputdir test_counting --pythonpath IFS_acceptance_tests/libs --dryrun --include Pending IFS_acceptance_tests/tests")

pending_tests = tidyUpPybotOutput(pending_tests_pybot_output)


print "Counting total tests..."

total_tests_pybot_output = shell("pybot --outputdir test_counting --pythonpath IFS_acceptance_tests/libs --dryrun IFS_acceptance_tests/tests")

total_tests = tidyUpPybotOutput(total_tests_pybot_output)


print "-------------------------------------------------------------------------"

print  "Test report:"

print "Tests running locally:", local_running_tests

print "Tests running against the dev server:", dev_running_tests

print "Tests marked as Failing:", failing_tests

print "Tests marked as Pending:", pending_tests

print "Grand test total:", total_tests

if tidy_failing_test_list:
  print ""
  print "We spotted some failing tests, here are the locations:"
  for test_location in tidy_failing_test_list:
    print test_location




