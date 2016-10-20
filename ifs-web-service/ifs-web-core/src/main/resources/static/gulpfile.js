// jshint ignore: start
var gulp = require('gulp');
var jshint = require('gulp-jshint');
var jscs = require('gulp-jscs');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var sass = require('gulp-sass');
var sassLint = require('gulp-sass-lint');
var compass = require('compass-importer');

gulp.task('default',['js','css']);

//build all js
gulp.task('js',['vendor','ifs-js']);

//concat and minify all the ifs files
gulp.task('ifs-js', function () {
  return gulp.src([
    'js/ifsCoreLoader.js',
    'js/ifs_modules/*.js',
    'js/ifs_pages/*.js',
    'js/fire.js'
  ])
  .pipe(jshint())
  .pipe(jshint.reporter('jshint-stylish'))
  // .pipe(jshint.reporter('fail'))
  .pipe(jscs())
  .pipe(jscs.reporter())
  // .pipe(jscs.reporter('fail'))
  .pipe(concat('ifs.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest('js/dest'))
});

//concat and minify all the vendor files
gulp.task('vendor',function(){
  return gulp.src([
    'js/vendor/cookie/*.js',
    'js/vendor/jquery/jquery-ui.min.js',
    'js/vendor/govuk/*.js',
    '!js/vendor/govuk/ie.js',
    'js/vendor/wysiwyg-editor/*.js',
    '!js/vendor/wysiwyg-editor/hallo-src/*.js',
  ])
  .pipe(concat('vendor.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest('js/dest'))
});

gulp.task('css', function () {
  return gulp.src('./sass/**/*.scss')
    .pipe(sassLint({
      files: {
        ignore: '**/{prototype,vendor}/**/*.scss'
      },
      config: '.sass-lint.yml'
    }))
    .pipe(sassLint.format())
    // .pipe(sassLint.failOnError())
    .pipe(sass({
        importer: compass,
        outputStyle: 'compressed'
      }).on('error', sass.logError))
    .pipe(gulp.dest('./css'));
});

gulp.task('css:watch', function () {
  gulp.watch('./sass/**/*.scss', ['css']);
});

gulp.task('js:watch', function () {
   gulp.watch(['js/**/*.js', '!js/dest/*.js'], ['js']);
});
