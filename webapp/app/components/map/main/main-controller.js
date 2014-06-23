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
    var mapModule = angular.module('gather.map');

    mapModule.controller('MapMainCtrl', MapMainCtrl); /**@ngInject*/
    function MapMainCtrl ($scope, $modal, events) {
        navigator.geolocation.watchPosition(angular.bind(this, this.onUserLocationChange));

        var createEventModal = $modal({
            scope: $scope,
            title: 'Create Event',
            contentTemplate: 'components/map/main/create-event.html',
            show: false
        });

        // Map Events
        this.events = events;

        var self = this;
        $scope.$watch('mapMainCtrl.events', function (events) {
          self.markers = [];
          console.log(events);
          angular.forEach(events, function (event) {

            var marker = new google.maps.Marker({
                title: event.name,
                position: new google.maps.LatLng(event.location.A, event.location.k),
                map: self.map
            });

            self.markers.push(marker);

            // events.$add({title: this.newEvent.name, position: this.newEvent.location});

            var infoWindow = new google.maps.InfoWindow({
                content: '<h3>' + event.name + '</h3>' +
                         '<p>The event is scheduled for ' + event.time  + '</p>' +
                         '<p>' + event.numberOfPeople + ' people are needed.</p>',
            });

            google.maps.event.addListener(marker, 'click', function () {
                infoWindow.open(self.map, marker);
            });
          });
        });

        this.mapConfig = {
            center: new google.maps.LatLng(0, 0),
            zoom: 19,
            disableDefaultUI: true,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            streetViewControl: false,
            mapTypeControl: false,
            panControl: false
        };

        this.createEvent = function ($event, params) {
            this.newEvent = {};
            this.newEvent.location = params[0].latLng;
            this.newEvent.time = new Date();
            createEventModal.show();
        };

        this.saveEvent = function () {
            var marker = new google.maps.Marker({
                title: this.newEvent.name,
                position: this.newEvent.location,
                map: this.map
            });

            // add the new event
            events.$add(this.newEvent);

            // events.$add({title: this.newEvent.name, position: this.newEvent.location});

            var infoWindow = new google.maps.InfoWindow({
                content: '<h3>' + this.newEvent.name + '</h3>' +
                         '<p>The event is scheduled for ' + this.newEvent.time  + '</p>' +
                         '<p>' + this.newEvent.numberOfPeople + ' people are needed.</p>',
            });

            var map = this.map;

            google.maps.event.addListener(marker, 'click', function () {
                infoWindow.open(map, marker);
            });

            createEventModal.hide();
        };
    }

    MapMainCtrl.prototype.onUserLocationChange = function (position) {
        if (this.userMarker) {
            this.userMarker.setPosition(new google.maps.LatLng(position.coords.latitude, position.coords.longitude));
        } else {
            this.userMarker = new google.maps.Marker({title: 'You!', position: new google.maps.LatLng(position.coords.latitude, position.coords.longitude), map: this.map});
        }
    };

})(window, window.angular);
