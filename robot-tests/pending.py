#!/usr/bin/python

import sys
import subprocess
import os
import fnmatch
import re
import getpass

def shell(command):
  process = subprocess.Popen(command.split(), stdout=subprocess.PIPE)
  output = process.communicate()[0]
  return output


def issue_is_done(issue, username, password):
  curl_string = "curl -u " + username + ":" + password + " https://devops.innovateuk.org/issue-tracking/rest/api/latest/issue/INFUND-" + issue + "?fields=status"
  response = shell(curl_string)
  return True if 'Done' in response else False


def main():
  username = str(raw_input("Please enter your Jira username: "))
  password = getpass.getpass()

  file_issue_tuples = []
  missing_tickets = []
  for path, dirs, files in os.walk(os.getcwd()):
    for filename in fnmatch.filter(files, "*.robot"):
      filepath = os.path.join(path, filename)
      f = open(filepath, 'r')
      for line in f:
        if '#TODO' in line:
          match = re.search("(\d\d\d\d)", line)
          if match:
            print "Checking " + match.group(0)
            if issue_is_done(match.group(0), username, password):
              file_issue_tuples.append([match.group(0), filename])
          else:
            missing_tickets.append([line, filepath])
      f.close()

  if file_issue_tuples:
    print "***************************************"
    print "Out of date pending tickets found, please see below:"
    for tuple in file_issue_tuples:
      print "Reference found to ticket INFUND-" + tuple[0] + " in file " + tuple[1] + " but this issue shows as done."


  if missing_tickets:
    print "***************************************"
    print "TODOs found with no ticket reference, please see below:"
    for tuple in missing_tickets:
      print "TODO found, but no ticket associated with it! The line is: " + tuple[0] + " in file " + tuple[1]



if __name__ == '__main__':
  main()

