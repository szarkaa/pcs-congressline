(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('CreditCardReportController', CreditCardReportController);

    CreditCardReportController.$inject = ['$scope', '$state', 'reportFilter', 'CreditCardReport', 'DTOptionsBuilder',
        'DTColumnDefBuilder', 'DateUtils', 'B64Encoder'];

    function CreditCardReportController ($scope, $state, reportFilter, CreditCardReport, DTOptionsBuilder,
        DTColumnDefBuilder, DateUtils, B64Encoder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtOptions.withOption('scrollX', true);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
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
            vm.reportFilter.transactionId = null;
            vm.reportFilter.programNumber = null;
            vm.reportFilter.fromDate = null;
            vm.reportFilter.toDate = null;
        }

        function buildReportFilter(reportFilter) {
            return {
                transactionId: reportFilter.transactionId,
                programNumber: reportFilter.programNumber,
                fromDate: DateUtils.convertLocalDateToServer(reportFilter.fromDate),
                toDate: DateUtils.convertLocalDateToServer(reportFilter.toDate)
            };
        }

        function search() {
            CreditCardReport.query({
                    query: B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)))
                },
                function (result) {
                    vm.reportList = result;
                }
            );
        }

        function downloadReportXls () {
            window.location.href = '/api/credit-card-report/download-report?query=' + B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)));
        }

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function isAnyFilterSet() {
            return vm.reportFilter.transactionId || vm.reportFilter.programNumber || vm.reportFilter.fromDate || vm.reportFilter.toDate;
        }
    }
})();
