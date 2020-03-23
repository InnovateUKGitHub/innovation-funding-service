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
var govukFrontendPath = nodeModulesPath + 'govuk-frontend/govuk/'
var sassFiles = [
  __dirname + '/sass/**/*.scss',
  govukFrontendPath + 'settings/**/*.scss',
  govukFrontendPath + 'tools/**/*.scss',
  govukFrontendPath + 'helpers/**/*.scss',
  govukFrontendPath + 'core/**/*.scss',
  govukFrontendPath + 'objects/**/*.scss',
  govukFrontendPath + 'components/**/*.scss',
  govukFrontendPath + 'utilities/**/*.scss',
  govukFrontendPath + 'overrides/**/*.scss'
]
var vendorJsFiles = [
  nodeModulesPath + 'js-cookie/src/js.cookie.js',
  nodeModulesPath + 'jquery/dist/jquery.js',
  nodeModulesPath + 'jquery-ui/ui/version.js',
  nodeModulesPath + 'jquery-ui/ui/focusable.js',
  nodeModulesPath + 'jquery-ui/ui/tabbable.js',
  nodeModulesPath + 'jquery-ui/ui/widget.js',
  nodeModulesPath + 'accessible-autocomplete/dist/accessible-autocomplete.min.js',
  govukFrontendPath + 'all.js',
  __dirname + '/js/vendor/govuk/application.js',
  __dirname + '/js/vendor/govuk/govuk-cookies.js',
  __dirname + '/js/vendor/wysiwyg-editor/*.js',
  __dirname + '/js/vendor/tablesorter/jquery.tablesorter.js',
  '!' + __dirname + '/js/vendor/wysiwyg-editor/hallo-src/*.js'
]

// copy over the css from accessible autocomplete css folder
gulp.task('web-core:copy-css-autocomplete', function () {
  return gulp.src(filesExist(nodeModulesPath + 'accessible-autocomplete/dist/accessible-autocomplete.min.css'))
  .pipe(gulp.dest(__dirname + '/css'))
})
// copy over the fonts from GDS node-modules to css/fonts folder
gulp.task('web-core:copy-fonts-govuk', function () {
  return gulp.src(filesExist(govukFrontendPath + 'assets/fonts/*'))
  .pipe(gulp.dest(__dirname + '/css/fonts'))
})
//  copy over the images from GDS node-modules to images folder
gulp.task('web-core:copy-images-govuk', function () {
  return gulp.src(filesExist(govukFrontendPath + 'assets/images/**/**'))
  .pipe(gulp.dest(__dirname + '/images'))
})
//  copy over tablesorter javascript to the javascript folder
gulp.task('web-core:copy-tablesorter', function () {
  return gulp.src(filesExist(nodeModulesPath + 'tablesorter/dist/js/jquery.tablesorter.js'))
  .pipe(gulp.dest(__dirname + '/js/vendor/tablesorter'))
})
//  copy over html5shiv javascript to the javascript folder
gulp.task('web-core:copy-html5shiv', function () {
  return gulp.src(filesExist(nodeModulesPath + 'html5shiv/dist/html5shiv.js'))
  .pipe(gulp.dest(__dirname + '/js/vendor/html5shiv'))
})

// concat and minify all the ifs files
gulp.task('web-core:ifs-js', function () {
  return gulp.src([
    __dirname + '/js/ifsCoreLoader.js',
    __dirname + '/js/ifs_modules/*.js',
    '!' + __dirname + '/js/ifs_modules/*.test.js',
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
gulp.task('web-core:js', gulp.series(gulp.parallel('web-core:copy-html5shiv', 'web-core:copy-tablesorter'),gulp.parallel('web-core:ifs-js', 'web-core:vendor')))

gulp.task('web-core:css', gulp.parallel('web-core:copy-css-autocomplete', 'web-core:copy-images-govuk', 'web-core:copy-fonts-govuk', function () {
  return gulp.src(filesExist(sassFiles))
    .pipe(sassLint({
      files: {
        ignore: [
          '**/layout/_gdsUpgrade.scss',
          '**/node_modules/**/*.scss'
        ]
      },
      config: __dirname + '/.sass-lint.yml'
    }))
    .pipe(sassLint.format())
    .pipe(sass({includePaths: [
        nodeModulesPath
    ],
      importer: compass,
      outputStyle: 'compressed'
    }).on('error', sass.logError))
    .pipe(replace('url(images/', 'url(/images/'))
    .pipe(gulp.dest(__dirname + '/css'))
}))

gulp.task('web-core:css:watch', function () {
  gulp.watch(__dirname + '/sass/**/*.scss', ['web-core:css'])
})

gulp.task('web-core:js:watch', function () {
  gulp.watch([__dirname + '/js/**/*.js', '!' + __dirname + '/js/**/*.test.js',  '!' + __dirname + '/js/dest/*.js'], ['web-core-js'])
})

gulp.task('default', gulp.parallel('web-core:js', 'web-core:css'))
