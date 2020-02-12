
# Accesibility Testing

### First time building

`cd accessibility-tests`
`npm install`

## Deploying

### Local

Note: by default the accessibility tests are configured  to point at local dev, however there are currently some SSO issues with this at the  moment
To  Run against a named environment  see below

`npm run cypress:open`  for interactive testing against local dev
`npm run  e2e` for headless running

### Remote 


`env CYPRESS_baseUrl=https://NAMED_ENV npm run cypress:open`

## Applicant crawl

`applicant-crawl.js` crawls the system as `steve.smith@empire.com`  and checks for accessibility issues. 




