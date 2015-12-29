var gulp = require('gulp');
var jshint = require('gulp-jshint');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');

gulp.task('default',['js']);

//build all js 
gulp.task('js',['ifs-js','govuk-js','editor-js']);


//concat and minify all the ifs files
gulp.task('ifs-js', function () {
   return gulp.src([
      'js/namespace.js',
   		'js/ifs_modules/*.js',
   		'js/ifs_pages/*.js',
   		'js/ifs.js'
   		])
      .pipe(jshint())
      .pipe(jshint.reporter('default'))
  	  .pipe(concat('ifs.min.js'))
      .pipe(uglify())
      .pipe(gulp.dest('js/dest'))
});

//concat and minify all the govuk files
gulp.task('govuk-js',function(){
   return gulp.src([
		'js/vendor/govuk/govuk-template.js',
		'js/vendor/govuk/details.polyfill.js',
    'js/vendor/govuk/selection-buttons.js',
    'js/vendor/govuk/application.js'
		])
  .pipe(concat('govuk.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest('js/dest'))
});

gulp.task('editor-js',function(){
  return gulp.src([
    'js/vendor/wysiwyg-editor/*.js'
  ])
  .pipe(concat('editor.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest('js/dest'))
});



gulp.task('watch', function () {
   gulp.watch('*.js', ['js']);
});