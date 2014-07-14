/**
 * Gather
 *
 * Gather is an application dedicated to bringing your immediate community to you.
 *
 * The external Angular dependencies for Gather include angular-strap and angular-ui-router.
 * Angular Strap is the angular port of bootstrap. Angular UI router is a replacement
 * for Angular's default ngRoute module.  It allows state management instead of route
 * management.
 *
 * The external libraries that Gather uses are currently only lodash.  Lodash is a library of
 * helper functions that are all held within the namespace `_`. So anytime you see the underscore
 * symbol followed by a dot, `_.`, a lodash utility method is being used.
 */
(function (window, angular, lodash, undefined) {    'use strict';
    /**
     * The list of external dependencies that will not be defined within this application
     * that Gather needs to be able to run.
     * @type {string[]}
     */
    var externalDependencies = ['mgcrea.ngStrap', 'ui.router'];

    /**
     * The list of child dependencies that will be defined within this application
     * that Gather needs to be stateful.
     * @type {string[]}
     */
    var childDependencies = ['gather.login', 'gather.map'];

    /**
     * Gather Module
     *
     * The Gather module is responsible for running the entire application.
     * It gathers all of its external and child dependencies and imports them
     * into the module. It is also is the wrapping scope of what the application will
     * bootstrap with `ng-app="gather"`.
     * @type {*|module}
     */
    var gatherModule = angular.module('gather', lodash.union(externalDependencies, childDependencies));

    /**
     * Lo-Dash
     *
     * Expose Lo-Dash as a dependency that can be injected within the application.
     * @constant
     */
    gatherModule.constant('_', lodash);


    gatherModule.service('auth', function ($rootScope, $location) {
      $rootScope.loggedIn = false;

      return new FirebaseSimpleLogin(
        new Firebase('https://gatherapp.firebaseio.com'),
        function (error, user) {
          if (error) {
            $rootScope.loggedIn = false;

          } else if (user) {
            $rootScope.loggedIn = true;

          } else {
            $rootScope.loggedIn = false;
          }

          $rootScope.$apply();
        }
      );
    });

    gatherModule.run(function ($rootScope, $location) {
      $rootScope.$watch('loggedIn', function (loggedIn) {
        if (!loggedIn) {
          $location.path('/login');
        } else if ($location.search().redirect) {
          $location.path($location.search().redirect);
          $location.search('redirect', null);
        } else {
          $location.path('/map');
        }
      });

      $rootScope.$watch(function () { return $location.path(); }, function (path) {
        if (!$rootScope.loggedIn && path != '/login') {
          $location.path('/login');
          $location.search('redirect', path);
        }
      });
    });

    window.onGoogleReady = function () {
        angular.bootstrap(window.document.body, ['gather']);
    };

})(window, window.angular, window._);
