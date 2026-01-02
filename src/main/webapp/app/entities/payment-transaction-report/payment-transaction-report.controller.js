(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('PaymentTransactionReportController', PaymentTransactionReportController);

    PaymentTransactionReportController.$inject = ['$scope', '$state', 'reportFilter', 'PaymentTransactionReport', 'DTOptionsBuilder',
        'DTColumnDefBuilder', 'DateUtils', 'B64Encoder'];

    function PaymentTransactionReportController ($scope, $state, reportFilter, PaymentTransactionReport, DTOptionsBuilder,
        DTColumnDefBuilder, DateUtils, B64Encoder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [1, 'desc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(0).notSortable()
        ];

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.fromDate = false;
        vm.datePickerOpenStatus.toDate = false;
        vm.openCalendar = openCalendar;

        vm.reportFilter = reportFilter;

        vm.search = search;
        vm.clear = clear;
        vm.isAnyFilterSet = isAnyFilterSet;
        vm.downloadReportXls = downloadReportXls;

        vm.reportList = [];

        function clear() {
            vm.reportList = [];
            vm.reportFilter.orderNumber = null;
            vm.reportFilter.transactionId = null;
            vm.reportFilter.fromDate = null;
            vm.reportFilter.toDate = null;
        }

        function buildReportFilter(reportFilter) {
            return {
                orderNumber: reportFilter.orderNumber,
                transactionId: reportFilter.transactionId,
                fromDate: DateUtils.convertLocalDateToServer(reportFilter.fromDate),
                toDate: DateUtils.convertLocalDateToServer(reportFilter.toDate)
            };
        }

        function search() {
            PaymentTransactionReport.query({
                    query: B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)))
                },
                function (result) {
                    vm.reportList = result;
                }
            );
        }

        function downloadReportXls () {
            window.location.href = '/api/payment-transaction-report/download-report?query=' + B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)));
        }

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function isAnyFilterSet() {
            return vm.reportFilter.orderNumber || vm.reportFilter.transactionId || vm.reportFilter.fromDate || vm.reportFilter.toDate;
        }
    }
})();
