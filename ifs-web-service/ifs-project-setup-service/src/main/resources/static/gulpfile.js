// jshint ignore: start
var gulp = require('gulp')
var standard = require('gulp-standard')
var uglify = require('gulp-uglify')
var concat = require('gulp-concat')

// build all js
gulp.task('js', function () {
	return gulp.src([
		'js/ifsProjectSetupLoader.js',
		'js/ifs_modules/*.js',
		'js/ifs_pages/*.js'
	])
	.pipe(standard())
	.pipe(concat('project-setup.min.js'))
	.pipe(uglify())
	.pipe(gulp.dest('js/dest'))
	.pipe(standard.reporter('default', {
		breakOnError: true,
		breakOnWarning: false,
		quiet: false
	}))
})
gulp.task('css', function (done) { done() })
gulp.task('css:watch', function () {})
gulp.task('default', gulp.parallel('js', 'css'))
