(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('HotelSummaryController', HotelSummaryController);

    HotelSummaryController.$inject = ['$scope', '$state', 'HotelSummary', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector', 'listFilter'];

    function HotelSummaryController ($scope, $state, HotelSummary, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector, listFilter) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [0, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
        ];

        vm.listFilter = listFilter;
        vm.getNightFromCell = getNightFromCell;
        vm.getTotalByReservationDate = getTotalByReservationDate;
        vm.downloadReportXls = downloadReportXls;

        vm.hotelSummary = [];

        load();

        function load() {
            if (vm.listFilter.congressHotel) {
                HotelSummary.get({
                        meetingCode: CongressSelector.getSelectedCongress().meetingCode,
                        hotelId: listFilter.congressHotel.hotelId
                    },
                    function (result) {
                        vm.hotelSummary = result;
                    }
                );
            }
        }

        function downloadReportXls () {
            window.location.href = '/api/hotel-summary/' + CongressSelector.getSelectedCongress().meetingCode
                + '/' + listFilter.congressHotel.hotelId.toString() + '/download-report';
        }

        function getNightFromCell (reservationDate, roomId) {
            var nights = 0;
            for (var i = 0; i < vm.hotelSummary.cells.length; i++) {
                var cell = vm.hotelSummary.cells[i];
                if (cell.roomId === roomId && cell.reservationDate - reservationDate === 0) {
                    nights = cell.nights;
                    break;
                }
            }
            return nights;
        }

        function getTotalByReservationDate (reservationDate) {
            var nights = 0;
            for (var i = 0; i < vm.hotelSummary.cells.length; i++) {
                var cell = vm.hotelSummary.cells[i];
                if (cell.reservationDate - reservationDate === 0) {
                    nights += cell.nights;
                 }
            }
            return nights;
        }

    }
})();
