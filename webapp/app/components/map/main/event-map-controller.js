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
function EventMap ($scope, $modal, events) {
  this.scope_ = $scope;
  this.modal_ = $modal({
    scope: $scope,
    contentTemplate: 'components/map/main/create-event.html',
    title: 'Create New Event',
    show: false
  });
  this.events_ = events;

  this.eventAlert_ = $modal({
    scope: $scope,
    contentTemplate: 'components/map/main/event-alert.html',
    title: 'Event Alert',
    show: false
  });

  this.map = undefined;
  this.mapConfiguration = {
    center: new google.maps.LatLng(40, 265),
    disableDefaultUI: true,
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    zoom: 3
  };

  this.userLocationMarker_ = undefined;
  navigator.geolocation.watchPosition(this.setUserLocationMarkerPosition.bind(this));

  this.markers = {};
  this.events_.$on('child_added', this.createEventMarker.bind(this));
  this.events_.$on('child_changed', this.updateEventMarker.bind(this));
  this.events_.$on('child_removed', this.deleteEventMarker.bind(this));

  this.infoWindow = undefined;
}

EventMap.prototype.showEventDetails = function ($params, event) {
  this.eventTemplate = event || {
    date: new Date(),
    latitude: $params[0].latLng.lat(),
    longitude: $params[0].latLng.lng()
  };

  this.modal_.show();
};

EventMap.prototype.createEvent = function ($event, $params) {
  this.events_.$add(this.eventTemplate);
  this.modal_.hide();
};

EventMap.prototype.updateEvent = function (event) {

};

EventMap.prototype.deleteEvent = function (event) {

};

EventMap.prototype.createEventMarker = function (event) {
  this.markers[event.snapshot.name] = new google.maps.Marker({
    map: this.map,
    position: {
      lat: event.snapshot.value.latitude,
      lng: event.snapshot.value.longitude
    }
  });

  if (this.userLocationMarker_) {
    var distance = google.maps.geometry.spherical.computeDistanceBetween (
        this.userLocationMarker_.position,
        this.markers[event.snapshot.name].position);

    // Limit distance to 300 meters
    if (distance < 300) {
      this.showEventAlert(event, distance);
    }
  }
};

EventMap.prototype.updateEventMarker = function (event) {
  this.markers[event.snapshot.name].setPosition({
    lat: event.snapshot.value.latitude,
    lng: event.snapshot.value.longitude
  });
};

EventMap.prototype.deleteEventMarker = function (event) {
  this.markers[event.snapshot.name].setMap(null);
};

EventMap.prototype.showEventAlert = function (event, distance) {
  this.activeEventAlert = event.snapshot;
  this.activeEventAlert.distance = distance.toFixed(2) + ' Meters Away';
  this.eventAlert_.show();
};

EventMap.prototype.joinEvent = function (event) {
  this.events_.$child(event.name).$update({
    numberOfPeople: event.value.numberOfPeople - 1
  });
  this.eventAlert_.hide();
};

EventMap.prototype.showEventMarkerInfoWindow = function (eventKey, marker) {
  this.infoWindow.event = this.events_[eventKey];
  this.infoWindow.open(this.map, marker);
};

EventMap.prototype.setUserLocationMarkerPosition = function (position) {
  position = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);

  if (this.userLocationMarker_ === undefined) {
    this.userLocationMarker_ = new google.maps.Marker({
      icon: 'http://i.stack.imgur.com/orZ4x.png',
      map: this.map,
      position: position
    });
    this.map.panTo(position);
    this.map.setZoom(18);
  } else {
    this.userLocationMarker_.setPosition(position);
  }
};

// Create the Angular event map controller
angular.module('gather.map').controller('EventMap', EventMap);

})(window, window.angular);
