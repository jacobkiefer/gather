(function (window, angular, undefined) { 'use strict';

/**
 * Event Map
 *
 * Event map displays the map to the users along with allowing the user to
 * create, update, and delete existing events.
 * @param $scope
 * @param $modal
 * @param events
 * @constructor
 * @ngInject
 */
function Login ($rootScope, $scope, $modal, auth) {
  this.scope_ = $scope;
  this.auth_ = auth;
  this.modal_ = $modal({
    backdrop: 'static',
    keyboard: false,
    scope: $scope,
    contentTemplate: 'components/login/login.html',
    title: 'Login'
  });

  $scope.$on('$destroy', this.modal_.destroy.bind(this.modal_));
}

Login.prototype.login = function () {
  this.auth_.login('password', this.userAccount);
};

Login.prototype.createAccount = function () {
  this.auth_.createUser(this.newUserAccount.email, this.newUserAccount.password, function (error, user) {
    console.log(error, user);
  });
};

Login.prototype.activeTab = 0;

Login.prototype.tabs = [
  {title: 'Login', template: 'components/login/login-form.html'},
  {title: 'Create Account', template: 'components/login/create-account-form.html'}
];

// Create the Angular event map controller
angular.module('gather.login').controller('Login', Login);

})(window, window.angular);
