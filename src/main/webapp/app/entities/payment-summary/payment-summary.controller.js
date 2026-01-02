(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('PaymentSummaryController', PaymentSummaryController);

    PaymentSummaryController.$inject = ['$scope', '$state', 'PaymentSummary', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector'];

    function PaymentSummaryController ($scope, $state, PaymentSummary, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [0, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [];

        vm.downloadReportXls = downloadReportXls;
        vm.reportList = [];

        loadReport();

        function loadReport() {
            PaymentSummary.query({congressId: CongressSelector.getSelectedCongress().id},
                function (result) {
                    vm.reportList = result;
                }
            );
        }

        function downloadReportXls () {
            window.location.href = '/api/payment-summary/' + CongressSelector.getSelectedCongress().id + '/download-report';
        }
    }
})();
