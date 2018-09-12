// jshint ignore: start
var gulp = require('gulp')
var standard = require('gulp-standard')
var uglify = require('gulp-uglify')
var concat = require('gulp-concat')

// build all js
gulp.task('competition-mgt:js', function () {
  return gulp.src([
    __dirname + '/js/ifsCompetitionManagementLoader.js',
    __dirname + '/js/ifs_modules/*.js',
    __dirname + '/js/ifs_pages/*.js'
  ])
  .pipe(standard())
  .pipe(concat('comp-management.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest(__dirname + '/js/dest'))
  .pipe(standard.reporter('default', {
    breakOnError: true,
    breakOnWarning: false,
    quiet: false
  }))
})
gulp.task('competition-mgt:css', function (done) { done() })
gulp.task('competition-mgt:css:watch', function () {})
gulp.task('default', gulp.parallel('competition-mgt:js', 'competition-mgt:css'))
