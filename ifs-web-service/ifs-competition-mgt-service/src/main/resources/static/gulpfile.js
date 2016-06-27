// jshint ignore: start
var gulp = require('gulp');
var jshint = require('gulp-jshint');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');

gulp.task('default',['js']);

//build all js
gulp.task('js',['test']);

//concat and minify all the ifs files
gulp.task('test', function () {
   return gulp.src([
      'js/*.js',
   		])
  	  .pipe(concat('test.min.js'))
      .pipe(uglify())
      .pipe(gulp.dest('js/dest'))
});
