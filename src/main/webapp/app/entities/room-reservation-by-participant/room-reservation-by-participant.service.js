(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('RoomReservationByParticipant', RoomReservationByParticipant);

    RoomReservationByParticipant.$inject = ['$resource', 'DateUtils'];

    function RoomReservationByParticipant ($resource, DateUtils) {
        var resourceUrl =  'api/room-reservation-by-participants/:meetingCode/:hotelId';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
