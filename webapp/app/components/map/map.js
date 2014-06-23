/**
 * Gather Map
 *
 */
(function (window, angular, undefined) {    'use strict';
    /**
     * Gather Map Module
     *
     * @type {*|module}
     */
    var mapModule = angular.module('gather.map', ['gather.templates', 'ui.map', 'firebase']);

    /**
     * Configuration of the Map Module
     *
     * This configuration will define the map state.
     * @ngInject
     */
    mapModule.config(function ($stateProvider) {
        /**
         * Map State
         *
         * The map state loads the main and navigation menu with their respective
         * controllers and templates.
         */
        $stateProvider.state({
            name: 'map',
            url: '/map',
            data: {},
            resolve: /**@ngInject*/ {
              events: function ($firebase) {
                return $firebase(new Firebase('https://gatherapp.firebaseio.com/events'));
              }
            },
            views: {
                 /*
                 * Main UI View targeted at root state.
                 * This view is located at `ui-view="main"` within index.html.
                 * This controller and template are found within `./main` directory.
                 */
                'main@': {
                    controller: 'EventMap as EventMap',
                    templateUrl: 'components/map/main/event-map.html'
                },

                /**
                 * Navigation Menu UI View targeted at root state.
                 * This view is located at `ui-view="navigation-menu"` within index.html.
                 * This controller and template are found within `./navigationmenu` directory.
                 */
                'navigation-menu@': {
                    // controller: 'MapNavigationMenuCtrl as mapNavigationMenuCtrl',
                    // templateUrl: 'components/map/navigationmenu/navigationmenu.html'
                }
            }
        });
    });

})(window, window.angular);
