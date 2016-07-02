const gulp = require('gulp');
const concat = require('gulp-concat');
const liveReload = require('livereload');

const DIR = {
  BUILD: 'build/',
  APP: 'app/src/'
};
const FILES = {
  HTML: DIR.APP + 'index.html',
  CSS: DIR.APP + '**/*.scss',
  JS: DIR.APP + '**/*.js'
};

gulp.task('default', ['build'], function () {});

gulp.task('build', ['build-html', 'build-css',
  'build-js'], function () {
  
});

gulp.task('build-html', function () {
  return gulp.src(FILES.HTML)
    .pipe(gulp.dest(DIR.BUILD));
});

gulp.task('build-css', function () {
  return gulp.src(FILES.CSS)
    .pipe(gulp.dest(DIR.BUILD));
});

gulp.task('build-js', function () {
  return gulp.src(FILES.JS)
    .pipe(concat('scripts.js'))
    .pipe(gulp.dest(DIR.BUILD));
});

var server = liveReload.createServer();
server.watch(DIR.BUILD);