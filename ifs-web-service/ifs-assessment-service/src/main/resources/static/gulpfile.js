// jshint ignore: start
var gulp = require('gulp');
var jshint = require('gulp-jshint');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');

gulp.task('default',['js','css']);

//build all js
gulp.task('js', function () {
   return gulp.src([
      'js/ifsAssessmentLoader.js',
      'js/ifs_modules/*.js',
      'js/ifs_pages/*.js',
   		])
      .pipe(jshint())
      .pipe(jshint.reporter('default'))
  	  .pipe(concat('assessment.min.js'))
      .pipe(uglify())
      .pipe(gulp.dest('js/dest'))
});
gulp.task('css', function () {});
gulp.task('css:watch', function () {});
