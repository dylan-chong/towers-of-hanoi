'use strict';

const gulp = require('gulp');
const concat = require('gulp-concat');
const sass = require('gulp-sass');
const liveReload = require('livereload');

const DIR = {
  BUILD: 'build/',
  SRC: 'src/'
};
const FILES = {
  HTML: DIR.SRC + 'index.html',
  SCSS: DIR.SRC + '**/*.scss',
  JS: DIR.SRC + '**/*.js'
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
  return gulp.src(FILES.SCSS)
    .pipe(sass().on('error', sass.logError))
    .pipe(gulp.dest(DIR.BUILD));
});

gulp.task('build-js', function () {
  return gulp.src(FILES.JS)
    .pipe(concat('scripts.js'))
    .pipe(gulp.dest(DIR.BUILD));
});

var server = liveReload.createServer();
server.watch(DIR.BUILD);