(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('RoomReservation', RoomReservation);

    RoomReservation.$inject = ['$resource', 'DateUtils'];

    function RoomReservation ($resource, DateUtils) {
        var resourceUrl =  'api/room-reservations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                url: 'api/registrations/:registrationId/room-reservations/:id',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.arrivalDate = DateUtils.convertLocalDateFromServer(data.arrivalDate);
                        data.departureDate = DateUtils.convertLocalDateFromServer(data.departureDate);
                    }
                    return data;
                }
            },
            'queryVMByRegistrationId': {
                method: 'GET',
                url: 'api/registrations/:id/room-reservation-dtos',
                isArray: true
            },
            'queryByRegistrationId': {
                method: 'GET',
                url: 'api/registrations/:id/room-reservations',
                isArray: true},
            'querySharedRoomReservations': {
                method: 'GET',
                url: 'api/congresses/:congressId/registrations/:registrationId/shared-room-reservations',
                isArray: true},
            'getByRoomReservationId': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.arrivalDate = DateUtils.convertLocalDateFromServer(data.arrivalDate);
                    data.departureDate = DateUtils.convertLocalDateFromServer(data.departureDate);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.arrivalDate = DateUtils.convertLocalDateToServer(copy.arrivalDate);
                    copy.departureDate = DateUtils.convertLocalDateToServer(copy.departureDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.arrivalDate = DateUtils.convertLocalDateToServer(copy.arrivalDate);
                    copy.departureDate = DateUtils.convertLocalDateToServer(copy.departureDate);
                    return angular.toJson(copy);
                }
            },
            'saveShared': {
                method: 'POST',
                url: 'api/room-reservations/shared'
            }
        });
    }
})();
