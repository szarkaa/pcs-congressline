(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RegFeeDetailsController', RegFeeDetailsController);

    RegFeeDetailsController.$inject = ['$scope', '$state', 'RegFeeDetails', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector'];

    function RegFeeDetailsController ($scope, $state, RegFeeDetails, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [];

        vm.downloadReportXls = downloadReportXls;
        vm.reportList = [];

        loadReport();

        function loadReport() {
            RegFeeDetails.query({congressId: CongressSelector.getSelectedCongress().id},
                function (result) {
                    vm.reportList = result;
                }
            );
        }

        function downloadReportXls () {
            window.location.href = '/api/regfee-details/' + CongressSelector.getSelectedCongress().id + '/download-report';
        }
    }
})();
