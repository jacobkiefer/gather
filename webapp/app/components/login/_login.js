/**
 * Gather login
 *
 */
(function (window, angular, undefined) {    'use strict';
    /**
     * Gather login Module
     *
     * @type {*|module}
     */
    var loginModule = angular.module('gather.login', ['gather.templates', 'firebase', 'ngSanitize']);

    /**
     * Configuration of the login Module
     *
     * This configuration will define the login state.
     * @ngInject
     */
    loginModule.config(function ($stateProvider) {
        /**
         * login State
         *
         * The login state loads the main and navigation menu with their respective
         * controllers and templates.
         */
        $stateProvider.state({
            name: 'login',
            url: '/login',
            data: {},
            resolve: /**@ngInject*/ {
            },
            views: {
                 /*
                 * Main UI View targeted at root state.
                 * This view is located at `ui-view="main"` within index.html.
                 * This controller and template are found within `./main` directory.
                 */
                'main@': {
                    controller: 'Login as Login'
                }
            }
        });
    });

})(window, window.angular);
