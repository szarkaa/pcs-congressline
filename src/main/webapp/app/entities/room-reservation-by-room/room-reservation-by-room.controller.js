(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RoomReservationByRoomController', RoomReservationByRoomController);

    RoomReservationByRoomController.$inject = ['$scope', '$state', 'RoomReservationByRoom', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector', 'listFilter'];

    function RoomReservationByRoomController ($scope, $state, RoomReservationByRoom, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector, listFilter) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [2, 'asc']);
        vm.dtOptions.withOption('scrollX', true);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
        ];

        vm.listFilter = listFilter;
        vm.downloadReportXls = downloadReportXls;

        vm.roomReservationByRooms = [];

        loadAll();

        function loadAll() {
            if (vm.listFilter.congressHotel) {
                RoomReservationByRoom.query({
                        meetingCode: CongressSelector.getSelectedCongress().meetingCode,
                        hotelId: listFilter.congressHotel.hotel.id
                    },
                    function (result) {
                        vm.roomReservationByRooms = result;
                    }
                );
            }
        }

        function downloadReportXls () {
            window.location.href = '/api/room-reservation-by-rooms/' + CongressSelector.getSelectedCongress().meetingCode
                + '/' + listFilter.congressHotel.hotel.id.toString() + '/download-report';
        }
    }
})();
