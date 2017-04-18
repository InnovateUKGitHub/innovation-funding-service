// jshint ignore: start
var gulp = require('gulp')
var standard = require('gulp-standard')
var uglify = require('gulp-uglify')
var concat = require('gulp-concat')
var sass = require('gulp-sass')
var sassLint = require('gulp-sass-lint')
var replace = require('gulp-replace')
var filesExist = require('files-exist')
var compass = require('compass-importer')

// Path variables
var nodeModulesPath = __dirname + '/../../../../../node_modules/'
var govukNodeModules = [
  nodeModulesPath + 'govuk_frontend_toolkit/**/*',
  nodeModulesPath + 'govuk_template_jinja/**/*',
  nodeModulesPath + 'govuk-elements-sass/**/*'
]

var vendorJsFiles = [
  nodeModulesPath + 'js-cookie/src/js.cookie.js',
  nodeModulesPath + 'jquery/dist/jquery.js',
  nodeModulesPath + 'simplestatemanager/src/ssm.js',
  'js/vendor/jquery-ui/jquery-ui.min.js',
  'js/vendor/govuk/*.js',
  '!js/vendor/govuk/ie.js',
  'js/vendor/wysiwyg-editor/*.js',
  '!js/vendor/wysiwyg-editor/hallo-src/*.js'
]

gulp.task('copy-govuk', ['copy-npm-govuk', 'copy-fonts-govuk', 'copy-images-govuk'])

gulp.task('copy-npm-govuk', function () {
  return gulp.src(filesExist(govukNodeModules, {checkGlobs: true}), {base: nodeModulesPath}).pipe(gulp.dest('sass/vendor/'))
})

// copy over the font from the template to our sourced controlled folder
gulp.task('copy-fonts-govuk', function () {
  return gulp.src(filesExist('sass/vendor/govuk_template_jinja/assets/stylesheets/fonts/*')).pipe(gulp.dest('css/fonts'))
})
//  copy over the images from the template  to our sourced controlled folder
gulp.task('copy-images-govuk', function () {
  return gulp.src(['sass/vendor/govuk_template_jinja/assets/images/**/**', 'sass/vendor/govuk_template_jinja/assets/stylesheets/images/**/**', 'sass/vendor/govuk_frontend_toolkit/images/**/**']).pipe(gulp.dest('images'))
})

gulp.task('default', ['js', 'css'])

// build all js
gulp.task('js', ['vendor', 'ifs-js'])

// concat and minify all the ifs files
gulp.task('ifs-js', function () {
  return gulp.src([
    'js/ifsCoreLoader.js',
    'js/ifs_modules/*.js',
    'js/ifs_pages/*.js',
    'js/fire.js'
  ])
  .pipe(standard())
  .pipe(concat('ifs.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest('js/dest'))
  .pipe(standard.reporter('default', {
    breakOnError: true,
    breakOnWarning: false,
    quiet: false
  }))
})

// concat and minify all the vendor files
gulp.task('vendor', function () {
  return gulp.src(filesExist(vendorJsFiles))
  .pipe(concat('vendor.min.js'))
  // .pipe(uglify())
  .pipe(gulp.dest('js/dest'))
})

gulp.task('css', ['copy-govuk'], function () {
  return gulp.src('./sass/**/*.scss')
    .pipe(sassLint({
      files: {
        ignore: [
          '**/prototype.scss',
          '**/{prototype,vendor}/**/*.scss'
        ]
      },
      config: '.sass-lint.yml'
    }))
    .pipe(sassLint.format())
    .pipe(sass({includePaths: [
      'sass/vendor/govuk_frontend_toolkit/stylesheets',
      'sass/vendor/govuk_template_jinja/assets/stylesheets',
      'sass/vendor/govuk-elements-sass/public/sass'
    ],
      importer: compass,
      outputStyle: 'compressed'
    }).on('error', sass.logError))
    .pipe(replace('url(images/', 'url(/images/'))
    .pipe(gulp.dest('./css'))
})

gulp.task('css:watch', function () {
  gulp.watch('./sass/**/*.scss', ['css'])
})

gulp.task('js:watch', function () {
  gulp.watch(['js/**/*.js', '!js/dest/*.js'], ['js'])
})
