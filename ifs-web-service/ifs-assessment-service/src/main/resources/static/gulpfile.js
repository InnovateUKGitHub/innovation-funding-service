// jshint ignore: start
var gulp = require('gulp')
var standard = require('gulp-standard')
var uglify = require('gulp-uglify')
var concat = require('gulp-concat')

// build all js
gulp.task('assessment:js', function () {
  return gulp.src([
    __dirname + '/js/ifsAssessmentLoader.js',
    __dirname + '/js/ifs_modules/*.js',
    __dirname + '/js/ifs_pages/*.js'
  ])
  .pipe(standard())
  .pipe(concat('assessment.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest(__dirname + '/js/dest'))
  .pipe(standard.reporter('default', {
    breakOnError: true,
    breakOnWarning: false,
    quiet: false
  }))
})
gulp.task('assessment:css', function (done) { done() })
gulp.task('assessment:css:watch', function () {})
gulp.task('default', gulp.parallel('assessment:js', 'assessment:css'))
