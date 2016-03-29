#Building front-end code

We use [Gulp](http://gulpjs.com/) as a task runner for all the front-end code. This tool makes sure that certain tasks like concatenating & minifing happen automatically.
 

##installation of Gulp
You have to follow the following steps:

1. Make sure you have npm installed. You can check this by typing `npm` in the commandline. If it isn't installed, you can install this by installing [NodeJs](https://nodejs.org/en/). Installing should be easy with just a gui installer. 
2. Install Gulp globally on your machine by running: `npm install gulp -g` , if this gives the error that you have to be and admin you can run `sudo install gulp -g`
3. Go to the folder that also contains this readme.md (ifs-web-service/src/main/resources/static) 
4. Install all dependencies by running `npm install` from the static folder. This will install all the plugins defined in the package.json. If your un into errors you might want to try the same command with sudo in front. 
5. Test if everything works by running `gulp` from the static folder. This will execute all the major build commands.  

##Building
All the build tasks available are described in  `gulpfile.js`. You can run these tasks individually with the syntax `gulp <task>` 