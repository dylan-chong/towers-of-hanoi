# Web Dev Template

A useful, forkable template for web development.  Currently, it only supports a
single page.

## Features

- Live reload
    - Open the index.html file in the `build/` directory and any changes made
      to files in the `src/` directory will be updated in the browser (which
      will automatically be refreshed).
- Automatic code compiling when changes are made
    - All SASS files are compiled and concatenated into one `build/styles.css`
      file.
    - All JS files are concatenated into one `build/scripts.js` file.
        - Dependencies (such as AngularJS) can be combined into that file as
          well (see `Dependencies` below).

## Installation

- [Download and install NodeJS](https://nodejs.org/en/) if you have not done so
  already.
- Install website dependencies from the with `bower install` (if Bower is not
  installed, run `npm install -g bower`).
- Install NodeJS dependencies with `npm install`.

# Usage

Running `gulp` builds the project from `src/` into `build/` and then opens
`build/index.html` in your default browser. Gulp will automatically watch for
changes in the `src/` file, rebuild the project, and refresh the browser.

## Website Dependencies

JS dependency files (e.g. AngularJS) can be specified in `gulpfile.js` (under
`DEPENDENCIES`), which then are concatenated, along with JS files in the `src/`
directory into the `build/scripts.js` file. To download more website
dependencies, use `bower install -S <your-package>` and then add the paths to
the necessary files to `gulpfile.js`
