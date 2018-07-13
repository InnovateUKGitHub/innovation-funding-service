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
var govukFrontendPath = nodeModulesPath + 'govuk-frontend/'
var sassFiles = [
  './sass/**/*.scss',
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
  nodeModulesPath + 'simplestatemanager/src/ssm.js',
  nodeModulesPath + 'jquery-ui-dist/jquery-ui.js',
	govukFrontendPath + 'all.js',
  'js/vendor/govuk/application.js',
  'js/vendor/wysiwyg-editor/*.js',
  '!js/vendor/wysiwyg-editor/hallo-src/*.js'
]

// copy over the fonts from GDS node-modules to css/fonts folder
gulp.task('copy-fonts-govuk', function () {
  return gulp.src(filesExist(govukFrontendPath + 'assets/fonts/*'))
  .pipe(gulp.dest('css/fonts'))
})
//  copy over the images from GDS node-modules to images folder
gulp.task('copy-images-govuk', function () {
  return gulp.src(filesExist(govukFrontendPath + 'assets/images/**/**'))
  .pipe(gulp.dest('images'))
})
//  copy over html5shiv javascript to the javascript folder
gulp.task('copy-html5shiv', function () {
  return gulp.src(filesExist(nodeModulesPath + 'html5shiv/dist/html5shiv.js'))
  .pipe(gulp.dest('js/vendor/html5shiv'))
})

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
gulp.task('js', gulp.parallel('vendor', 'ifs-js', 'copy-html5shiv'))

gulp.task('css', gulp.parallel('copy-images-govuk', 'copy-fonts-govuk', function () {
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
		    nodeModulesRelativePath
    ],
      importer: compass,
      outputStyle: 'compressed'
    }).on('error', sass.logError))
    .pipe(replace('url(images/', 'url(/images/'))
    .pipe(gulp.dest('./css'))
}))

gulp.task('css:watch', function () {
  gulp.watch('./sass/**/*.scss', ['css'])
})

gulp.task('js:watch', function () {
  gulp.watch(['js/**/*.js', '!js/dest/*.js'], ['js'])
})

gulp.task('default', gulp.parallel('js', 'css'))
