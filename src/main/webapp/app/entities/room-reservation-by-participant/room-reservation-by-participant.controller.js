(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RoomReservationByParticipantController', RoomReservationByParticipantController);

    RoomReservationByParticipantController.$inject = ['$scope', '$state', 'RoomReservationByParticipant', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector', 'listFilter'];

    function RoomReservationByParticipantController ($scope, $state, RoomReservationByParticipant, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector, listFilter) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtOptions.withOption('scrollX', true);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).notSortable()
        ];

        vm.listFilter = listFilter;
        vm.downloadReportXls = downloadReportXls;

        vm.roomReservationByParticipants = [];

        loadAll();

        function loadAll() {
            if (vm.listFilter.congressHotel) {
                RoomReservationByParticipant.query({
                        meetingCode: CongressSelector.getSelectedCongress().meetingCode,
                        hotelId: listFilter.congressHotel.hotel.id
                    },
                    function (result) {
                        vm.roomReservationByParticipants = result;
                    }
                );
            }
        }

        function downloadReportXls () {
            window.location.href = '/api/room-reservation-by-participants/' + CongressSelector.getSelectedCongress().meetingCode
                + '/' + listFilter.congressHotel.hotel.id.toString() + '/download-report';
        }
    }
})();
