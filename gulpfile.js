'use strict';

// **************** DEPENDENCIES **************** //

const gulp = require('gulp');
const concat = require('gulp-concat');
const sass = require('gulp-sass');
const liveReload = require('livereload');

// **************** CONSTANTS **************** //

const DIR = {
  BUILD: 'build/',
  SRC: 'src/'
};
const FILES = {
  HTML: DIR.SRC + 'index.html',
  SCSS: DIR.SRC + '**/*.scss',
  JS: DIR.SRC + '**/*.js'
};
const DEPENDENCIES = {
  JS: ['bower_components/angular/angular.min.js']
};

// **************** TASKS **************** //

gulp.task('default', ['watch'], function () {});

gulp.task('serve', ['build'], function () {
  var server = liveReload.createServer();
  server.watch(DIR.BUILD);
  console.log('Live-reload is watching "' + DIR.BUILD + '"');
});

gulp.task('watch', ['serve'], function () {
  gulp.watch(DIR.SRC + '**/*', ['build']);
});

// ---------------- BUILD ---------------- //

gulp.task('build', ['build-html', 'build-css',
  'build-js'], function () {
  console.log('Finished building');
});

gulp.task('build-html', function () {
  return gulp.src(FILES.HTML)
    .pipe(gulp.dest(DIR.BUILD));
});

gulp.task('build-css', function () {
  return gulp.src(FILES.SCSS)
    .pipe(sass().on('error', sass.logError))
    .pipe(concat('styles.css'))
    .pipe(gulp.dest(DIR.BUILD));
});

gulp.task('build-js', function () {
  return gulp.src(DEPENDENCIES.JS.concat([FILES.JS]))
    .pipe(concat('scripts.js'))
    .pipe(gulp.dest(DIR.BUILD));
});
