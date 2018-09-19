// jshint ignore: start
var gulp = require('gulp')
var standard = require('gulp-standard')
var uglify = require('gulp-uglify')
var concat = require('gulp-concat')

// build all js
gulp.task('eu-grant-registration:js', function () {
  return gulp.src([
    __dirname + '/js/euGrantRegistrationLoader.js',
    __dirname + '/js/ifs_pages/*.js'
  ])
  .pipe(standard())
  .pipe(concat('eu-grant-registration.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest(__dirname + '/js/dest'))
  .pipe(standard.reporter('default', {
    breakOnError: true,
    breakOnWarning: false,
    quiet: false
  }))
})