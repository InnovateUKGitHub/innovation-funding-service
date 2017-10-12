FROM mhart/alpine-node:8.5
RUN npm install --save @frctl/fractal
RUN npm i -g @frctl/fractal
ADD fractal-components/ fractal-components/
ADD static/ static/
ADD fractal.js fractal.js
CMD fractal start --sync