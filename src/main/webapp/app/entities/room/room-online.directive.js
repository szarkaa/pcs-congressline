(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('onlineRoomQuantityValidation', onlineRoomQuantityValidation);

    function onlineRoomQuantityValidation() {
        var directive = {
            scope: {
                roomId: "=",
                hotelRooms: "=",
                arrivalDate: "="
            },
            require: 'ngModel',
            restrict: 'A',
            link: linkFunc
        };

        return directive;

        function linkFunc($scope, $element, $attrs, $ctrl) {
            function validation(value) {
                var room = getRoomById($scope.hotelRooms, $scope.roomId);

                if (!value || !room || !$scope.arrivalDate) {
                    $ctrl.$setValidity('quantity', true);
                    return value;
                }

                var departureDate = new Date(value);
                departureDate.setHours(0, 0, 0, 0);

                for (var i = 0; room && room.reservations && i < room.reservations.length; i++) {
                    var reservation = room.reservations[i];
                    var reservationDate = new Date(reservation.reservationDate);
                    reservationDate.setHours(0, 0, 0, 0);
                    var originalArrivalDate = $scope.arrivalDate;
                    $scope.arrivalDate = null;
                    $scope.arrivalDate = originalArrivalDate;
                    //iterate over selected arrival and departure dates
                    var arrivalDate = new Date($scope.arrivalDate);
                    arrivalDate.setHours(0, 0, 0, 0);
                    for (; arrivalDate < departureDate; arrivalDate.setDate(arrivalDate.getDate() + 1)) {
                        if (reservationDate.getTime() === arrivalDate.getTime() && reservation.reserved >= room.quantity) {
                            $ctrl.$setValidity('quantity', false);
                            return value;
                        }
                    }
                    $ctrl.$setValidity('quantity', true);
                }
                return value;
            }

            $ctrl.$parsers.push(validation);
        }
    }

    function getRoomById(hotelRooms, id) {
        if (!hotelRooms) return null;
        for (var i = 0; i < hotelRooms.length; i++) {
            var hotel = hotelRooms[i];
            for (var j = 0; j < hotel.rooms.length; j++) {

                if (hotel.rooms[j].single && hotel.rooms[j].single.id == id) {
                    return hotel.rooms[j].single;
                }

                if (hotel.rooms[j].double && hotel.rooms[j].double.id == id) {
                    return hotel.rooms[j].double;
                }
            }
        }
        return null;
    }

})();
