# Native Docker Setup 

For a more optimal experience (without consuming all of your computer's RAM), install the native Docker application.

The various OS flavours can be found from:

- [Mac](https://docs.docker.com/engine/installation/mac/)
- [Linux](https://docs.docker.com/engine/installation/linux/)
- [Windows](https://docs.docker.com/docker-for-windows/) - Requires HyperV (only with Windows Professional and above)

This has not been tested on Windows/MacOS so your mileage may vary considerably (at current).

You could run this setup from within your own custom VM setup (maybe something like CoreOS). 

## Setting Up

1. Make sure you don't have any processes running on your machine that may have conflicting ports with the 
application stack. The main ports you may be concerned with are listed in the following table:
    
    | Port  | Service       |
    | ----- | ------------- |
    | 443   | Shib (HTTPS)  |
    | 389   | Shib (LDAP)   |
    | 8080  | Web           |
    | 8090  | Data          |
    | 38606 | MySQL         |
    | 9876  | IMAP          |
    | 1234  | Webmail       |
    
    There are some ports you can change (at your own discretion). These are detailed in `.env-defaults`. To modify them, 
    copy the contents into a `.env` file and then modify the ports. However, the important ports necessary for the
    infrastructure to function properly cannot be changed.

2. Run `hosts-helper.sh` script (or manually setup your `hosts` file for Windows):
        
        ./hosts-helper.sh

3. Run the `setup.sh` script:

        ./setup.sh
        
## Usage

Standard Docker + Compose usage applies from this point onwards. If in doubt, some utility scripts have been provided 
for ease of consumption:

- `start.sh` starts up all your containers. This should typically be done when you want to start working on the project. 

- `stop.sh` stops all the containers. This should typically be done when your stop working on the project or 
before shutting down. Bear in mind that the entire environment is relatively resource intensive (even at idle) and it 
is advisable not to have them running all the time (eating your RAM).

- `teardown.sh` removes the existing Docker containers for the project. Don't run this unless you want to purposefully
want to destroy your environment e.g. in the event of a rebuild.

## Additional information

The following scripts 

- `setup.sh` builds the entire environment using Docker and Docker Compose. Then creates the required databases in the 
 `mysql` container, runs the Flyway migrations, syncs the `shib` and `mysql` user databases and finally runs the 
  Gradle deployment tasks via `deploy.sh` (see below).
    
- `hosts-helper.sh` appends the IP forwards to your `/etc/hosts` if you run a *nix system. If you run Windows, you will 
have to manually append the IPs to your `hosts` file which will be in `C:/Windows/system32/drivers/etc/hosts` (after 
changing permissions for the file). 

- `deploy.sh` is the main deployment script to build the entire application within the Docker environment. It 
essentially just runs a `./gradlew cleanDeploy` on a sub-project.
    
    Usage as follows:

        ./deploy.sh {all|data|web|asm|comp-mgt|app|ps|psm} {gradleOpts}
        
    Note: `gradleOpts` is not required. If you do specify some options they will be passed to the sub-project's 
    `cleanDeploy` task run.
    
- `frontend.sh` runs various Gulp tasks (for building CSS/JS files) across all the projects (this might be better 
located somewhere else).

    Usage as follows:

        ./frontend.sh {all|css|js|js-core|js-ps|js-comp-mgt|js-app|js-ass}
        
- `syncShib.sh` syncs the `shib` container users with the `mysql` container database (`ifs-database`).

## Troubleshooting

 - In the event of catastrophic failure of your Docker containers, rebuild the entire environment by running 
 `teardown.sh` and then `setup.sh`.
 
 - There are potential issues during `setup.sh` execution involving the creation of the required MySQL schemas 
 (specifically `ifs-test`). The following errors will be observable during execution:
 
        ERROR 2002 (HY000): Can't connect to local MySQL server through socket '/tmp/mysql.sock' (2)
        
        ...
        
        Unable to clean unknown schema: `ifs_test`
 
    Running `teardown.sh` followed by running `setup.sh` is recommended (aka try again).