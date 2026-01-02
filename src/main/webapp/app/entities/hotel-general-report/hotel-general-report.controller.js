(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('HotelGeneralReportController', HotelGeneralReportController);

    HotelGeneralReportController.$inject = ['$scope', '$state', 'HotelGeneralReport', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector'];

    function HotelGeneralReportController ($scope, $state, HotelGeneralReport, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtOptions.withOption('scrollX', true);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
        ];

        vm.downloadReportXls = downloadReportXls;

        vm.hotelGeneralReports = [];

        loadAll();

        function loadAll() {
            HotelGeneralReport.query({
                    meetingCode: CongressSelector.getSelectedCongress().meetingCode
                },
                function (result) {
                    vm.hotelGeneralReports = result;
                }
            );
        }

        function downloadReportXls () {
            window.location.href = '/api/hotel-general-report/' + CongressSelector.getSelectedCongress().meetingCode + '/download-report';
        }
    }
})();
