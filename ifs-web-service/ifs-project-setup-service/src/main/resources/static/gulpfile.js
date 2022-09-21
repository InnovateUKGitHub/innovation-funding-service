// jshint ignore: start
var gulp = require('gulp')
var standard = require('gulp-standard')
var uglify = require('gulp-uglify')
var concat = require('gulp-concat')

// build all js
gulp.task('project-setup:js', function () {
  return gulp.src([
    __dirname + '/js/ifsProjectSetupLoader.js',
    __dirname + '/js/ifs_modules/*.js',
    __dirname + '/js/ifs_pages/*.js'
   	])
    .pipe(standard())
    .pipe(concat('project-setup.min.js'))
    .pipe(uglify())
    .pipe(gulp.dest(__dirname + '/js/dest'))
    .pipe(standard.reporter('default', {
      breakOnError: true,
      breakOnWarning: false,
      quiet: false
    }))
})
gulp.task('project-setup:css', function (done) { done() })
gulp.task('project-setup:css:watch', function () {})
gulp.task('default', gulp.parallel('project-setup:js', 'project-setup:css'))
