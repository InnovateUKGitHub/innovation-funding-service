FROM node
RUN npm install --save @frctl/fractal
RUN npm i -g @frctl/fractal
ADD fractal-components/ fractal-components/
ADD static/ static/
ADD fractal.js fractal.js
ADD gulpfile.js gulpfile.js
ADD package.json package.json
ADD yarn.lock yarn.lock
CMD fractal start --sync