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
var gdsFrontendToolkitPath = nodeModulesPath + 'govuk_frontend_toolkit/'
var gdsTemplateJinjaPath = nodeModulesPath + 'govuk_template_jinja/'
var gdsElementsPath = nodeModulesPath + 'govuk-elements-sass/'
var vendorImages = [
	gdsTemplateJinjaPath + 'assets/images/**/**',
	gdsTemplateJinjaPath + 'assets/stylesheets/images/**/**',
  gdsFrontendToolkitPath + 'images/**/**'
]
var sassFiles = [
  __dirname + '/sass/**/*.scss',
  gdsFrontendToolkitPath + '**/*.scss',
  gdsElementsPath + '**/*.scss'
]
var vendorJsFiles = [
  nodeModulesPath + 'js-cookie/src/js.cookie.js',
  nodeModulesPath + 'jquery/dist/jquery.js',
  nodeModulesPath + 'simplestatemanager/src/ssm.js',
  __dirname + '/js/vendor/jquery-ui/jquery-ui.min.js',
  gdsFrontendToolkitPath + 'javascripts/govuk/shim-links-with-button-role.js',
  gdsFrontendToolkitPath + 'javascripts/vendor/polyfills/bind.js',
  gdsFrontendToolkitPath + 'javascripts/govuk/details.polyfill.js',
  __dirname + '/js/vendor/govuk/application.js',
  gdsTemplateJinjaPath + 'assets/javascripts/govuk-template.js',
  __dirname + '/js/vendor/wysiwyg-editor/*.js',
  '!' + __dirname + '/js/vendor/wysiwyg-editor/hallo-src/*.js'
]

// copy over the vendor javascript files to the js/vendor/govuk folder
gulp.task('web-core:copy-js-govuk', function () {
	return gulp.src(filesExist([
	  gdsTemplateJinjaPath + 'assets/javascripts/govuk-template.js',
    gdsTemplateJinjaPath + 'assets/javascripts/ie.js'
  ]))
  .pipe(gulp.dest(__dirname + '/js/vendor/govuk'))
})
// copy over the fonts from GDS node-modules to css/fonts folder
gulp.task('web-core:copy-fonts-govuk', function () {
  return gulp.src(filesExist(gdsTemplateJinjaPath + 'assets/stylesheets/fonts/*'))
  .pipe(gulp.dest(__dirname + '/css/fonts'))
})
//  copy over the images from GDS node-modules to images folder
gulp.task('web-core:copy-images-govuk', function () {
  return gulp.src(filesExist(vendorImages))
  .pipe(gulp.dest(__dirname + '/images'))
})
gulp.task('web-core:copy-govuk', gulp.parallel('web-core:copy-js-govuk', 'web-core:copy-fonts-govuk', 'web-core:copy-images-govuk'))

// concat and minify all the ifs files
gulp.task('web-core:ifs-js', function () {
  return gulp.src([
    __dirname + '/js/ifsCoreLoader.js',
    __dirname + '/js/ifs_modules/*.js',
    __dirname + '/js/ifs_pages/*.js',
    __dirname + '/js/fire.js'
  ])
  .pipe(standard())
  .pipe(concat('ifs.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest(__dirname + '/js/dest'))
  .pipe(standard.reporter('default', {
    breakOnError: true,
    breakOnWarning: false,
    quiet: false
  }))
})

// concat and minify all the vendor files
gulp.task('web-core:vendor', function () {
  return gulp.src(filesExist(vendorJsFiles))
  .pipe(concat('vendor.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest(__dirname + '/js/dest'))
})

// build all js
gulp.task('web-core:js', gulp.series('web-core:copy-govuk', gulp.parallel('web-core:vendor', 'web-core:ifs-js')))

gulp.task('web-core:css', function () {
  return gulp.src(filesExist(sassFiles))
    .pipe(sassLint({
      files: {
        ignore: [
          '**/prototype.scss',
          '**/prototype/**/*.scss',
          '**/node_modules/**/*.scss'
        ]
      },
      config: __dirname + '/.sass-lint.yml'
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
    .pipe(gulp.dest(__dirname + '/css'))
})

gulp.task('web-core:css:watch', function () {
  gulp.watch(__dirname + '/sass/**/*.scss', ['web-core:css'])
})

gulp.task('web-core:js:watch', function () {
  gulp.watch([__dirname + '/js/**/*.js', '!' + __dirname + '/js/dest/*.js'], ['web-core-js'])
})

gulp.task('default', gulp.parallel('web-core:js', 'web-core:css'))
