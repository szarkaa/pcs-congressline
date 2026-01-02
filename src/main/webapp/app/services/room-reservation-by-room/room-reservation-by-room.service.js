(function() {
    'use strict';

    angular.module('pcsApp')
        .factory('RoomReservationByRoomFilter', RoomReservationByRoomFilter);

    RoomReservationByRoomFilter.$inject = ['$cookies', '$state'];

    function RoomReservationByRoomFilter ($cookies, $state) {
        var roomReservationByRoomFilter = null;

        return {
            setRoomReservationByRoomFilter: function (filter) {
                if (filter) {
                    roomReservationByRoomFilter = {
                        congressHotel: filter.congressHotel
                    };
                    $cookies.putObject('room-reservation-by-room-filter', roomReservationByRoomFilter);
                }
                else {
                    roomReservationByRoomFilter = null;
                    $cookies.remove('room-reservation-by-room-filter');
                }

            },
            getRoomReservationByRoomFilter: function () {
                if (roomReservationByRoomFilter == null) {
                    roomReservationByRoomFilter = $cookies.getObject('room-reservation-by-room-filter');
                    if (!roomReservationByRoomFilter) {
                        return {
                            congressHotel: null
                        };
                    }
                }
                return roomReservationByRoomFilter;
            }
        };
    }
})();
