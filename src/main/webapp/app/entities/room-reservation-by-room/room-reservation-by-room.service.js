(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('RoomReservationByRoom', RoomReservationByRoom);

    RoomReservationByRoom.$inject = ['$resource', 'DateUtils'];

    function RoomReservationByRoom ($resource, DateUtils) {
        var resourceUrl =  'api/room-reservation-by-rooms/:meetingCode/:hotelId';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
