# Changelog
All notable changes to the docker containers will be documented in this file.

### 2021-02-10
 - Check if messages being analyzed by API scan scripts are globally excluded or not.

### 2021-02-01
 - Allow more flexibility to specify ZAP command line options when using Webswing:
  - The default options stay as `-host 0.0.0.0 -port 8090` unless
  - You specify an env var `ZAP_WEBSWING_OPTS` in which case that replaces the defaults
  - If not then if a `/zap/wrk/owasp_zap_root_ca.key` file exists then this is loaded as the ZAP root cert
  - If not then if the `/zap/wrk` is writable then ZAP will output the public and private ZAP cert into that directory
  
### 2021-01-19
 - Python 3.5 is no longer supported.

### 2020-12-23
 - Update Webswing to download prod version if valid key supplied.

### 2020-12-16
 - Update Webswing to latest version (20.2.1) to work with newer Java versions.
 - Update Java in stable image to version 11.

### 2020-12-11
 - Add `target` parameter to `ajaxSpider.scan_as_user` call. Without it ajaxSpider crawls first included in a context URL and not a target which is set.

### 2020-12-02
 - Use `ARG` command (for `DEBIAN_FRONTEND`) instead of `ENV` so that the parameter does not persist after the build process has been completed.

### 2020-11-27
 - Move logging level of Params from `info` to `debug`, as it can contain sensitive data when authenticated scans are run.
 
### 2020-11-24
 - Add support for authenticated scans.

### 2020-11-19
 - Add zap_tune function (disable all tags and limit pscan alerts to 10), zap_tuned hook and disable recovery log.

### 2020-11-16
 - Update zap-api-scan.py to add support for GraphQL.

### 2020-10-13
 - Alert_on_Unexpected_Content_Types.js > Added Content-Type application/health+json to the list of expected types.
 
### 2020-09-18
 - Fail immediately if the spider scans were not started to provide better error message.

###  2020-08-28
 - Packaged scans will use the provided context when spidering and active scanning.

###  2020-08-27
 - Updated to use webswing 2.5.12

###  2020-08-03
 - Add `IS_CONTAINERIZED` environment variable to the container image, used in the python script to check for containerized environments (e.g. containerd) without relying on container runtime specific files.

###  2020-07-17
 - Make podman compatible

###  2020-05-20
 - Make docker stable use ubuntu 20.04

###  2020-05-13
 - Make `python` command use Python 3.

### 2020-05-12
 - Removed python 2, only python 3 will be supported going forward.

### 2020-04-27
- Add `application/vnd.api+json` to the list of expected API content types.

### 2020-04-08
- Changed zap-full-scan.py and zap-api-scan.py to include the -I option to ignore only warning used by zap-baseline-scan.py

### 2020-04-06
- Make API scan policy available to the root user, otherwise it would fail to start the active scan.

### 2020-04-01
- Changed live and weekly images to use Java 11.

### 2020-02-21
 - Changed zap-full-scan.py, zap-api-scan.py, and zap-baseline-scan.py to include the missing check for markdown file.

### 2020-02-07
 - Change zap-full-scan.py and zap-api-scan.py to be Python3 compatible

### 2020-01-22
 - Change `live`, `stable`, and `weekly` images to set the locale and lang to `C.UTF-8`,
 to improve interoperability with Python 3 (e.g. `zap-cli`).

### 2019-10-16
 - Added response code after each URL reported on standard out:

```
WARN-NEW: Web Browser XSS Protection Not Enabled [10016] x 4 
	https://www.example.com/ (200 OK)
	https://www.example.com/robots.txt (404 Not Found)
	https://www.example.com (200 OK)
	https://www.example.com/sitemap.xml (404 Not Found)
```

### 2019-10-01
 - Added Python3 and the pip3 version of ZAP in preparation for Python 2 EOL: https://www.python.org/dev/peps/pep-0373/

### 2019-09-05
 - Changed zap-full-scan.py to ignore example ascan rules

### 2019-06-18
 - Changed zap-full-scan.py to always include the active scan beta rules
 - Changed zap-full-scan.py to include the active scan alpha rules when the -a switch is used
 - Fixed ownership of all files in the /zap directory
 - Added this changelog
