const gulp = require('gulp');
const concat = require('gulp-concat');
const liveReload = require('livereload');
const mkdirp = require('mkdirp');

const DIR = {
  BUILD: 'build/',
  SRC: 'app/src/'
};
const FILES = {
  HTML: DIR.SRC + 'index.html',
  CSS: DIR.SRC + '**/*.css',
  JS: DIR.SRC + '**/*.js'
};

gulp.task('default', ['build'], function () {

});

gulp.task('build', ['make-build-dir', 'build-html',
  'build-css', 'build-js'], function () {
});

gulp.task('make-build-dir', function (done) {
  mkdirp(DIR.BUILD, function (error) {
    if (!error) console.log('Made build directory');
    else console.error('Error making build directory at "' 
      + DIR.BUILD + '"', error);
    
    done();
  });
});

gulp.task('build-html', ['make-build-dir'], function () {
  return gulp.src(FILES.HTML)
    .pipe()
});

gulp.task('build-css', ['make-build-dir'], function () {

});

gulp.task('build-js', ['make-build-dir'], function () {

});


var server = liveReload.createServer();
server.watch(DIR.BUILD);