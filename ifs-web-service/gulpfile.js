var gulp = require( 'gulp' )
var chug = require( 'gulp-chug' )
var gulpfiles = './**/src/main/resources/static/gulpfile.js'

gulp.task( 'js', function () {
    // Find and run all gulpfiles under all subdirectories
    return gulp.src(gulpfiles)
        .pipe(chug({
        tasks:  [ 'js' ],
        }))
})

gulp.task( 'css', function () {
    // Find and run all gulpfiles under all subdirectories
    return gulp.src(gulpfiles)
        .pipe(chug({
        tasks:  [ 'css' ],
        }))
})
gulp.task( 'css:watch', function () {
    // Find and run all gulpfiles under all subdirectories
    return gulp.src(gulpfiles)
        .pipe(chug({
        tasks:  [ 'css:watch' ],
        }))
})
gulp.task('default', gulp.parallel('js','css'))
