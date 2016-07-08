// jshint ignore: start
var gulp = require('gulp');
var jshint = require('gulp-jshint');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');

gulp.task('default',['js']);

//build all js
gulp.task('js',['management-js']);

//concat and minify all the ifs files
gulp.task('management-js', function () {
   return gulp.src([
      'js/ifsCompetitionManagementLoader.js',
      'js/ifs_pages/*.js',
   		])
      .pipe(jshint())
      .pipe(jshint.reporter('default'))
  	  .pipe(concat('comp-management.min.js'))
      .pipe(uglify())
      .pipe(gulp.dest('js/dest'))
});
