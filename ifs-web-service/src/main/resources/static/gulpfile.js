var gulp = require('gulp');
var jshint = require('gulp-jshint');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');

gulp.task('default',['js']);
 
gulp.task('js', function () {
   return gulp.src(['js/ifs_modules/*.js','js/ifs.js','js/ifs-loader.js'])
      .pipe(jshint())
      .pipe(jshint.reporter('default'))
	  .pipe(uglify())
	  .pipe(concat('ifs.min.js'))
      .pipe(gulp.dest('js'))
});

gulp.task('watch', function () {
   gulp.watch('js/ifs_modules/*.js', ['js']);
});