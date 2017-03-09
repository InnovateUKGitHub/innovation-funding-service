#!/usr/bin/python

import subprocess
import re
import sys
import urllib
from urlparse import urlparse
from HTMLParser import HTMLParser


# Implement bash commands inside python
def shell(command):
  process = subprocess.Popen(command.split(), stdout=subprocess.PIPE)
  output = process.communicate()[0]
  return output

# Download a file
def downloadFile(downloadUrl, downloadFileLocation):
  curlCommand = "curl --insecure " + downloadUrl + " -o " + downloadFileLocation
  shell(curlCommand)

def main():
  if len(sys.argv) != 2:
    print "[*] Usage: ./downloadpublic.py [download_url] [filename]"
    print "[*] eg ./downloadpublic.py https://ifs-local-dev/management/competition/10/applications/download downloaded_files/submitted_applications.xlsx"
    sys.exit()
  downloadUrl = sys.argv[1] # e.g. https://ifs-local-dev/management/competition/1/download
  downloadFileLocation = sys.argv[2] # e.g. /tmp/file.xlsx
  downloadFile(downloadUrl, downloadFileLocation)

if __name__ == '__main__':
  main()