'use strict';

/* Create a new Fractal instance and export it for use elsewhere if required */
const fractal = module.exports = require('@frctl/fractal').create();
const mandelbrot = require('@frctl/mandelbrot');

const myCustomisedTheme = mandelbrot({
    skin: 'black',
    panels: ["notes", "html", "context", "resources", "info"]
});

/* Fractal project config
----------------------------------------------------------------------------- */

/* Set the title of the project */
fractal.set('project.title', 'Innovate UK Front-end');

/* Fractal components
----------------------------------------------------------------------------- */

/* Tell Fractal where the components will live */
fractal.components.set('path', __dirname + '/fractal-components');

/* Set component file extensions */
fractal.components.set('ext', '.html');

/* Status to apply to all components unless overridden */
fractal.components.set('default.status', 'wip');

/* Fractal docs
----------------------------------------------------------------------------- */

/* Tell Fractal where the documentation pages will live */
fractal.docs.set('path', __dirname + '/docs');

/* Set the file extension for documentation files */
fractal.docs.set('ext', '.md')

/* Fractal web UI
----------------------------------------------------------------------------- */

/* pass custom config to theme */
fractal.web.theme(myCustomisedTheme);

/* Specify a directory of static assets */
fractal.web.set('static.path', __dirname + '/static');

/* Specify a default preview file */
fractal.components.set('default.preview', '@preview');

/* Set the static HTML build destination */
//fractal.web.set('builder.dest', __dirname + '/ifs-web-core/src/main/resources/templates/fractal-build');
