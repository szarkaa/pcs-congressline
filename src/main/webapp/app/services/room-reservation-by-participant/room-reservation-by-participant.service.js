(function() {
    'use strict';

    angular.module('pcsApp')
        .factory('RoomReservationByParticipantFilter', RoomReservationByParticipantFilter);

    RoomReservationByParticipantFilter.$inject = ['$cookies', '$state'];

    function RoomReservationByParticipantFilter ($cookies, $state) {
        var roomReservationByParticipantFilter = null;

        return {
            setRoomReservationByParticipantFilter: function (filter) {
                if (filter) {
                    roomReservationByParticipantFilter = {
                        congressHotel: filter.congressHotel
                    };
                    $cookies.putObject('room-reservation-by-participant-filter', roomReservationByParticipantFilter);
                }
                else {
                    roomReservationByParticipantFilter = null;
                    $cookies.remove('room-reservation-by-participant-filter');
                }

            },
            getRoomReservationByParticipantFilter: function () {
                if (roomReservationByParticipantFilter == null) {
                    roomReservationByParticipantFilter = $cookies.getObject('room-reservation-by-participant-filter');
                    if (!roomReservationByParticipantFilter) {
                        return {
                            congressHotel: null
                        };
                    }
                }
                return roomReservationByParticipantFilter;
            }
        };
    }
})();
