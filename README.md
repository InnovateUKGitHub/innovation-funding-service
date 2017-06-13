# Innovation Funding Service Project


## Front-end development

[![Standard - JavaScript Style Guide](https://img.shields.io/badge/code%20style-standard-brightgreen.svg)](http://standardjs.com/)

### Requirements

- [Node.js](https://nodejs.org/en/) >= 6.x
- [Yarn](https://yarnpkg.com/en/docs/install) >= 0.x

### Installation

1. Install global dependencies `yarn global add gulp`
2. Run `yarn` from the directory level containing `package.json` (Should be found in `/ifs-web-service`)

### Build

1. Run `gulp` from the directory level containing the main `gulpfile.js` (Should be found in `/ifs-web-service`)

You can run gulp tasks individually with `gulp <task>` e.g.: `gulp css`

You can also run gulp tasks for a specific service by running `gulp` from the service directory i.e.: `/ifs-web-service/ifs-assessment-service/src/main/resources/static`

### Documentation

A styleguide for the IFS front-end components is available using [Fractal](http://fractal.build/).

#### Installation

1. Having the Fractal CLI will help, `npm install -g @frctl/fractal`

#### Build

1. `cd ifs-web-service/`
2. Run a local Fractal server `fractal start --sync`


## Important links:

* [Devops Links](https://devops.innovateuk.org/)
