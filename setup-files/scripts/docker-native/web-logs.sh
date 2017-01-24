#!/usr/bin/env bash
docker-compose -p ifs logs -ft --tail 100 web
