'use strict';

// **************** DEPENDENCIES **************** //

const gulp = require('gulp');
const concat = require('gulp-concat');
const sass = require('gulp-sass');
const liveReload = require('livereload');
const opn = require('opn');

// **************** CONSTANTS **************** //

const DIR = {
  BUILD: 'build/',
  SRC: 'src/'
};
const FILES = {
  HOME_PAGE_HTML: DIR.BUILD + 'index.html',
  HTML: DIR.SRC + 'index.html',
  SCSS: DIR.SRC + '**/*.scss',
  JS: DIR.SRC + '**/*.js'
};
const DEPENDENCIES = {
  JS: ['bower_components/angular/angular.min.js']
};

// **************** TASKS **************** //

gulp.task('default', ['open-home-page']);

gulp.task('serve', ['build'], function () {
  var server = liveReload.createServer();
  server.watch(DIR.BUILD);
  console.log('Live-reload is watching "' + DIR.BUILD + '"');
});

gulp.task('watch', ['serve'], function () {
  gulp.watch(DIR.SRC + '**/*', ['build']);
});

gulp.task('open-home-page', ['watch'], function () {
  opn(FILES.HOME_PAGE_HTML);
});

// ---------------- BUILD ---------------- //

gulp.task('build', ['build-html', 'build-css', 'build-js'], function () {
  console.log('Finished building');
});

gulp.task('build-html', function () {
  return gulp.src(FILES.HTML)
    .pipe(gulp.dest(DIR.BUILD));
});

gulp.task('build-css', function () {
  return gulp.src(FILES.SCSS)
    .pipe(concat('styles.css'))
    .pipe(sass().on('error', sass.logError))
    .pipe(gulp.dest(DIR.BUILD));
});

gulp.task('build-js', function () {
  return gulp.src(DEPENDENCIES.JS.concat([FILES.JS]))
    .pipe(concat('scripts.js'))
    .pipe(gulp.dest(DIR.BUILD));
});
