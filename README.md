# Innovation Funding Service Project

Grant management system.

# Quick Installation

Before starting make sure you have the following installed

* Linux or MacOS environment
* Docker - increase Docker memory allocation to at least 8GiB
* Java 8

Update your /etc/hosts file to include

```
127.0.0.1 ifs.local-dev
127.0.0.1 auth.local-dev
```

### Running in skaffold

The commented aliases file contains shortcuts for dev tasks
```
source os-files/kustomize/aliases.sh
skaffold_e
skaffold_dx
```

