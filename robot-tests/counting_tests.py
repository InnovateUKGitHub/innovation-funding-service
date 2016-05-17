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

# Clean up grep's output to give a tidy test suite for each failing test
def tidyUpFailingTestSearch(grep_output):
  list_of_failing_test_locations = []
  for match in re.findall("(\S*)\:", grep_output):
    list_of_failing_test_locations.append(match)
  return list_of_failing_test_locations


# Clean up grep's output to give a sorted list of test suites containing pending tests
def tidyUpPendingTestSearch(grep_output):
  list_of_pending_test_locations = []
  for match in re.findall("(IFS_acceptance_tests\S*)\:", grep_output):
    list_of_pending_test_locations.append(match)
  list_of_pending_test_locations = sorted(set(list_of_pending_test_locations))
  return list_of_pending_test_locations


# Attempt to fetch documenting comments from test suites containing pending tests
def grabDocumentingComments(suite_list):
  suite_comment_tuples = []
  for suite in suite_list:
    f = open(suite)
    for line in f:
      for match in re.findall("[\s]*(\# Pending [^\n]*)", line):
        suite_comment_tuples.append((suite, match))
    f.close()
  return suite_comment_tuples



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

if pending_tests != "0 tests":
  pending_test_list = shell("grep -R Pending IFS_acceptance_tests/")
  tidy_pending_test_list = tidyUpPendingTestSearch(pending_test_list)
  print tidy_pending_test_list
  pending_document_tuples = grabDocumentingComments(tidy_pending_test_list)

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

print ""

if tidy_failing_test_list:
  print "We spotted some failing tests, here are the locations:"
  for test_location in tidy_failing_test_list:
    print test_location
else:
  print "We didn't search for any failing test locations, as no failing tests were found. Let's keep it that way!"

print ""

if tidy_pending_test_list:
  print "We spotted some pending tests, here's an attempt to return the documenting comments (note that one comment will often refer to several tests):"
  for toop in pending_document_tuples:
    print toop[0]
    print toop[1]
else:
  print "We didn't search for any pending test documenting comments, as no pending tests were found. Go find some bugs!"



