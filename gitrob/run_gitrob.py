#!/bin/env python

import sys
import json
import pprint
import subprocess
import time
import ast
import os

amountOfArgs = 8

if len(sys.argv) != amountOfArgs + 1:
    print("Exactly %s arguments needed. You provided the following %s:" % (amountOfArgs, len(sys.argv) - 1))
    for a in sys.argv:
        print("- " + a)
    print("Exiting.")
    sys.exit(2)

gitHubUserName = str(sys.argv[1])
accessToken = str(sys.argv[2])
dockerRegUser = str(sys.argv[3])
dockerRegPass = str(sys.argv[4])
repoToScan = str(sys.argv[5])
commitDepth = str(sys.argv[6])
whiteList = str(sys.argv[7])
breakOnSuspicious = int(sys.argv[8])
logFile = 'log.json'
htmlFile = 'log.html'
tempFile = 'outFile'

try:
    os.remove(tempFile)
except OSError:
    pass

try:
    os.remove(logFile)
except OSError:
    pass

try:
    os.remove(htmlFile)
except OSError:
    pass

p=subprocess.Popen(["../setup-files/scripts/copy-from-nexus-container.sh", dockerRegUser, dockerRegPass, "gitrob"])
p.wait()

with open(tempFile, 'a') as outFile:
    gitRobProcess = subprocess.Popen(["./gitrob", "-github-access-token", accessToken, "-commit-depth", commitDepth, "-save", logFile, gitHubUserName], stdout=outFile)

print("Please wait for GitRob to finish scanning...")

killWord = b'Ctrl+C'
gitRobRunning = True
dots = "*"

while gitRobRunning:
    outPutTail = subprocess.check_output(['tail', '-3', tempFile])
    if killWord not in outPutTail:
        time.sleep(2)
        print(dots)
        dots = dots + "*"
    else:
        gitRobProcess.kill()
        gitRobRunning = False

with open(logFile) as data_file:
    data_loaded = json.load(data_file)

pp = pprint.PrettyPrinter(indent=2)

anySuspicious = 0

if data_loaded['Findings'] != None:
    with open(htmlFile, 'a+') as logHtml:
        i = 1
        for finding in data_loaded['Findings']:
            if finding['RepositoryName'] == repoToScan:
                if finding['FilePath'].split('/')[-1] not in whiteList:
                    anySuspicious = 1
                    print("\n##### Suspicious item #%s\n" % i)
                    pp.pprint(ast.literal_eval(json.dumps(finding)))
                    logHtml.write(str(ast.literal_eval(json.dumps(finding))))
                    i = i + 1

if anySuspicious == 1:
    if breakOnSuspicious == 1:
        print("\nSuspicious item(s) found, exiting with error code 1.\n")
        sys.exit(1)
    else:
        print("\nSuspicious item(s) found, breaking not enforced, exiting with 0.\n")
        sys.exit(0)
else:
    print("Nothing risky found. Exiting with 0.")
    sys.exit(0)

