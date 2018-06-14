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
var nodeModulesRelativePath = '../../../../../node_modules/'
var gdsFrontendToolkitPath = nodeModulesPath + 'govuk_frontend_toolkit/'
var gdsTemplateJinjaPath = nodeModulesPath + 'govuk_template_jinja/'
var gdsElementsPath = nodeModulesPath + 'govuk-elements-sass/'
var vendorImages = [
	gdsTemplateJinjaPath + 'assets/images/**/**',
	gdsTemplateJinjaPath + 'assets/stylesheets/images/**/**',
  gdsFrontendToolkitPath + 'images/**/**'
]
var sassFiles = [
  './sass/**/*.scss',
  gdsFrontendToolkitPath + '**/*.scss',
  gdsElementsPath + '**/*.scss'
]
var vendorJsFiles = [
  nodeModulesPath + 'js-cookie/src/js.cookie.js',
  nodeModulesPath + 'jquery/dist/jquery.js',
  nodeModulesPath + 'simplestatemanager/src/ssm.js',
  'js/vendor/jquery-ui/jquery-ui.min.js',
  gdsFrontendToolkitPath + 'javascripts/govuk/shim-links-with-button-role.js',
  gdsFrontendToolkitPath + 'javascripts/vendor/polyfills/bind.js',
  gdsFrontendToolkitPath + 'javascripts/govuk/details.polyfill.js',
  'js/vendor/govuk/application.js',
  gdsTemplateJinjaPath + 'assets/javascripts/govuk-template.js',
  'js/vendor/wysiwyg-editor/*.js',
  '!js/vendor/wysiwyg-editor/hallo-src/*.js'
]

// copy over the vendor javascript files to the js/vendor/govuk folder
gulp.task('copy-js-govuk', function () {
	return gulp.src(filesExist([
	  gdsTemplateJinjaPath + 'assets/javascripts/govuk-template.js',
    gdsTemplateJinjaPath + 'assets/javascripts/ie.js'
  ]))
  .pipe(gulp.dest('js/vendor/govuk'))
})
// copy over the fonts from GDS node-modules to css/fonts folder
gulp.task('copy-fonts-govuk', function () {
  return gulp.src(filesExist(gdsTemplateJinjaPath + 'assets/stylesheets/fonts/*'))
  .pipe(gulp.dest('css/fonts'))
})
//  copy over the images from GDS node-modules to images folder
gulp.task('copy-images-govuk', function () {
  return gulp.src(filesExist(vendorImages))
  .pipe(gulp.dest('images'))
})
gulp.task('copy-govuk', gulp.parallel('copy-js-govuk', 'copy-fonts-govuk', 'copy-images-govuk'))

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
  .pipe(uglify())
  .pipe(gulp.dest('js/dest'))
})

// build all js
gulp.task('js', gulp.series('copy-govuk', gulp.parallel('vendor', 'ifs-js')))

gulp.task('css', function () {
  return gulp.src(filesExist(sassFiles))
    .pipe(sassLint({
      files: {
        ignore: [
          '**/prototype.scss',
          '**/prototype/**/*.scss',
          nodeModulesRelativePath + '**/*.scss'
        ]
      },
      config: '.sass-lint.yml'
    }))
    .pipe(sassLint.format())
    .pipe(sass({includePaths: [
      gdsFrontendToolkitPath + 'stylesheets',
      gdsElementsPath + 'public/sass',
      gdsTemplateJinjaPath + 'assets/stylesheets'
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

gulp.task('default', gulp.parallel('js', 'css'))
