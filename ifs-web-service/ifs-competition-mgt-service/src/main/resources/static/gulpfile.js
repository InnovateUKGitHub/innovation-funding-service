// jshint ignore: start
var gulp = require('gulp');
var jshint = require('gulp-jshint');
var jscs = require('gulp-jscs');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');

gulp.task('default',['js','css']);

//build all js
gulp.task('js', function () {
   return gulp.src([
      'js/ifsCompetitionManagementLoader.js',
      'js/ifs_modules/*.js',
      'js/ifs_pages/*.js',
   	])
    .pipe(jshint())
    .pipe(jshint.reporter('jshint-stylish'))
    // .pipe(jshint.reporter('fail'))
    .pipe(jscs())
    .pipe(jscs.reporter())
    // .pipe(jscs.reporter('fail'))
    .pipe(concat('comp-management.min.js'))
    .pipe(uglify())
    .pipe(gulp.dest('js/dest'))
});
gulp.task('css', function () {});
gulp.task('css:watch', function () {});
