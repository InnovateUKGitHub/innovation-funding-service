// jshint ignore: start
var gulp = require('gulp')
var standard = require('gulp-standard')
var uglify = require('gulp-uglify')
var concat = require('gulp-concat')

gulp.task('default', ['js', 'css'])

// build all js
gulp.task('js', function () {
  return gulp.src([
    'js/ifsApplicationLoader.js',
    'js/ifs_modules/*.js',
    'js/ifs_pages/*.js'
  ])
  .pipe(standard())
  .pipe(concat('application.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest('js/dest'))
  .pipe(standard.reporter('default', {
    breakOnError: true,
    breakOnWarning: false,
    quiet: false
  }))
})
gulp.task('css', function () {})
gulp.task('css:watch', function () {})
