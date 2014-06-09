/**
 * Gulp: The Streaming Build System.
 *
 * Hello and welcome to the MTAC Build System!
 * This build system is what compiles all of the javascript files,
 * auto-prefixes all of our css, dynamically injects for Angular, and
 * oodles of other useful tasks!
 * @param gulp
 * @param plugins
 * @param path
 * @param promise
 */
(function Gulpfile (gulp, gulprc, bowerrc, plugins, path, promise) { 'use strict';
    /**
     * True if the files should be built for production.
     * @type {Boolean}
     */
    var isProductionBuild = plugins.util.env.production || false;

    /**
     * True if the files should be copied to their respective
     * Laravel counterparts.
     * @type {Boolean}
     */
    var copyToLaravel = plugins.util.env.laravel || false;

    /**
     * Target directory for piping the compiled files.
     * @type {string}
     */
    var targetDirectory = isProductionBuild ? './production/' : './build/';

    /**
     * Image directory relative to app directory.
     * @type {string}
     */
    var imageDirectory = path.normalize(path.relative('./app/', gulprc.globs.images.replace(/\*/g, '')) + '/');

    // Define the list of tasks that can be called.
    // There are two possible build systems for each task.
    // The first build system is `build` it is the default task
    // and should be used during development.  The second build
    // system is `production`, it minifies all of the code and
    // prepares it for a production release.
    // There is an additional flag that can be used in addition
    // to each build system.  This flag is `laravel` and will
    // automatically copy the files into the relevant Laravel locations.
    gulp.task('default', ['clean', 'watch', 'connect']);
    gulp.task('assets', assets);
    gulp.task('bower', bower);
    gulp.task('clean', clean);
    gulp.task('connect', ['index'], connect());
    gulp.task('index', ['assets', 'javascript', 'sass', 'templates'], index);
    gulp.task('javascript', javascript);
    gulp.task('sass', sass);
    gulp.task('templates', templates);
    gulp.task('watch', watch);

    function assets () {
        return gulp.src(gulprc.globs.images)
            .pipe(gulp.dest(targetDirectory + './images/'))
            .pipe(plugins.if(copyToLaravel, gulp.dest(gulprc.laravel.public + gulprc.laravel.prefix + './images/')));
    }

    function bower () {
        return plugins.bowerFiles({env: isProductionBuild ? 'production' : 'build'})
            .pipe(gulp.dest(targetDirectory + bowerrc.directory))
            .pipe(plugins.if(copyToLaravel, gulp.dest(gulprc.laravel.public + gulprc.laravel.prefix + bowerrc.directory)));
    }

    function clean () {
        return gulp.src([targetDirectory, copyToLaravel ? gulprc.laravel.public + gulprc.laravel.prefix : '', copyToLaravel ? gulprc.laravel.views + gulprc.laravel.prefix : ''], {read: false})
            .pipe(plugins.clean({force: copyToLaravel}));
    }

    function connect () {
        return plugins.connectMulti().server({root: [targetDirectory], livereload: gulprc.server.livereload, port: gulprc.server.port});
    }

    function index () {
        var basePath = path.normalize(process.cwd() + '/' + targetDirectory);
        gulp.src(gulprc.globs.index)
            .pipe(plugins.inject(plugins.bowerFiles().pipe(plugins.filter('**/*.js')), {ignorePath: basePath, starttag: '<!-- inject:bower -->'}))
            .pipe(plugins.inject(gulp.src([isProductionBuild ? './**/*.min.js' : './**/*.js', './**/*.css', '!'+bowerrc.directory + './**/*'], {cwd: targetDirectory, read: false}), {ignorePath: basePath}))
            .pipe(plugins.replace(/[\t\s]*<!-- .*inject.* -->[\t]*/g, ''))
            .pipe(gulp.dest(targetDirectory));

        // When copying to laravel, the file prefix needs to be updated when injecting.
        if (copyToLaravel) {
            gulp.src(gulprc.globs.index)
                .pipe(plugins.inject(plugins.bowerFiles().pipe(plugins.filter('**/*.js')), {addPrefix: gulprc.laravel.prefix, ignorePath: basePath, starttag: '<!-- inject:bower -->', transform: indexScriptTransformer}))
                .pipe(plugins.inject(gulp.src([isProductionBuild ? './**/*.min.js' : './**/*.js', './**/*.css', '!'+bowerrc.directory + './**/*'], {cwd: targetDirectory, read: false}), {addPrefix: gulprc.laravel.prefix, ignorePath: basePath, transform: indexScriptTransformer}))
                .pipe(plugins.replace(/[\t\s]*<!-- .*inject.* -->[\t]*/g, ''))
                .pipe(plugins.replace(imageDirectory, path.join(gulprc.laravel.prefix, imageDirectory)))
                .pipe(plugins.rename('index.php'))
                .pipe(gulp.dest(gulprc.laravel.views + gulprc.laravel.prefix));
        }
    }

    function indexScriptTransformer (filepath, file, index, length) {
        switch (path.extname(filepath)) {
            case '.css': return '<link rel="stylesheet" href="' + path.normalize(filepath) + '">';
            case '.html': return '<link rel="import" href="' + path.normalize(filepath) + '">';
            case '.js': return '<script src="' + path.normalize(filepath) + '"></script>';
            default: return;
        }
    }

    function javascript () {
        return gulp.src([gulprc.globs.javascript, '!'+gulprc.globs.tests])
            .pipe(plugins.plumber())
            .pipe(plugins.jshint(true))
            .pipe(plugins.jshint.reporter('jshint-stylish'))
            .pipe(plugins.concat('app.js', {newLine: '\r\n\r\n'}))
            .pipe(plugins.plumber.stop())
            .pipe(gulp.dest(targetDirectory))
            .pipe(plugins.if(copyToLaravel, gulp.dest(gulprc.laravel.public + gulprc.laravel.prefix)))
            .pipe(plugins.if(isProductionBuild, plugins.ngAnnotate()))
            .pipe(plugins.if(isProductionBuild, plugins.rename('app.min.js')))
            .pipe(plugins.if(isProductionBuild, plugins.uglify({outSourceMap: true})))
            .pipe(plugins.if(isProductionBuild, gulp.dest(targetDirectory)))
            .pipe(plugins.if(copyToLaravel, gulp.dest(gulprc.laravel.public + gulprc.laravel.prefix)));
    }

    function sass () {
        return gulp.src(gulprc.globs.sass)
            .pipe(plugins.plumber())
            .pipe(plugins.rubySass({loadPath: ['./app/sass/', bowerrc.directory], style: isProductionBuild ? 'compressed' : 'expanded', precision: 10}))
            .pipe(plugins.autoprefixer())
            .pipe(plugins.plumber.stop())
            .pipe(gulp.dest(targetDirectory))
            .pipe(plugins.if(copyToLaravel, plugins.replace(path.normalize(bowerrc.directory), path.join(gulprc.laravel.prefix, bowerrc.directory))))
            .pipe(plugins.if(copyToLaravel, plugins.replace(imageDirectory, path.join(gulprc.laravel.prefix, imageDirectory))))
            .pipe(plugins.if(copyToLaravel, gulp.dest(gulprc.laravel.public + gulprc.laravel.prefix)));
    }

    function templates () {
        return gulp.src(gulprc.globs.templates)
            .pipe(plugins.angularTemplatecache('app-templates.js', {module: gulprc.name.toLowerCase() + '.templates', root: 'components/', standalone: true}))
            .pipe(gulp.dest(targetDirectory))
            .pipe(plugins.if(isProductionBuild, plugins.rename('app-templates.min.js')))
            .pipe(plugins.if(isProductionBuild, plugins.uglify({outSourceMap: true})))
            .pipe(plugins.if(isProductionBuild, gulp.dest(targetDirectory)))
            .pipe(plugins.if(copyToLaravel, gulp.dest(gulprc.laravel.public + gulprc.laravel.prefix)));
    }

    function watch () {
            plugins.watch({glob: bowerrc.json}, ['bower', 'index']);
            plugins.watch({glob: gulprc.globs.images, read: false}, ['assets']);
            plugins.watch({glob: gulprc.globs.index}, ['index']);
            plugins.watch({glob: [gulprc.globs.javascript, '!'+gulprc.globs.tests]}, ['javascript', 'index']);
            plugins.watch({glob: gulprc.globs.sass}, ['sass', 'index']);
            plugins.watch({glob: gulprc.globs.templates}, ['templates', 'index']);
    }

})(require('gulp'), JSON.parse(require('fs').readFileSync('./.gulpfilerc', {encoding: 'UTF8'})), JSON.parse(require('fs').readFileSync('./.bowerrc', {encoding: 'UTF8'})), require('gulp-load-plugins')(), require('path'), require('q'));