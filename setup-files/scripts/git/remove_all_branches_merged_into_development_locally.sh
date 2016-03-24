#!/usr/bin/env bash

git checkout development                                                                            # Switch to development
git branch --merged | grep -v "\*" | grep -v master | grep -v dev | xargs -n 1 git branch -d        # Remove merged branches except master and dev (some people use dev instead of master so being safe)
