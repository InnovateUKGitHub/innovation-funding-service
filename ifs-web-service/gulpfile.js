var gulp = require( 'gulp' );
var chug = require( 'gulp-chug' );
var gulpfiles = './**/src/main/resources/static/gulpfile.js';

gulp.task('default',['js','css']);

gulp.task( 'js', function () {
    // Find and run all gulpfiles under all subdirectories
    gulp.src(gulpfiles)
        .pipe(chug({
        tasks:  [ 'js' ],
        }));
});

gulp.task( 'css', function () {
    // Find and run all gulpfiles under all subdirectories
    gulp.src(gulpfiles)
        .pipe(chug({
        tasks:  [ 'css' ],
        }));
});
