(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('FinancialReportController', FinancialReportController);

    FinancialReportController.$inject = ['$scope', '$state', 'reportFilter', 'FinancialReport', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'B64Encoder', 'Confirmation'];

    function FinancialReportController ($scope, $state, reportFilter, FinancialReport, DTOptionsBuilder, DTColumnDefBuilder, B64Encoder, Confirmation) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [2, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtOptions.withOption('scrollY', '500px');
        vm.dtOptions.withOption('scrollX', true);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).notSortable()
        ];

        vm.reportFilter = reportFilter;
        vm.downloadReportXls = downloadReportXls;
        vm.printConfirmation = printConfirmation;

        vm.participantsToPay = false;
        vm.financialReports = [];

        vm.loadAll = loadAll;

        vm.loadAll();

        function loadAll() {
            FinancialReport.query({
                    query: B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)))
                },
                function (result) {
                    vm.financialReports = result;
                }
            );
        }

        function buildReportFilter(reportFilter) {
            return {
                participantsToPay: reportFilter.participantsToPay,
                congressId: reportFilter.congressId
            };
        }

        function downloadReportXls () {
            window.location.href = '/api/financial-report/download?query=' + B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)));
        }

        function printConfirmation(registrationId, countryCode) {
            Confirmation.printConfirmation(createConfirmationForPrinting(registrationId, countryCode, ''));
        }

        function createConfirmationForPrinting(registrationId, countryCode, customConfirmationEmail) {
            var conf = {};
            conf.language = countryCode && countryCode !== 'HU' ? 'en' : 'hu';
            conf.confirmationTitleType = 'CONFIRMATION';
            conf.optionalText = '';
            conf.registrationId = registrationId;
            conf.customConfirmationEmail = customConfirmationEmail;
            conf.ignoredChargeableItemIdList = [];
            conf.ignoredChargedServiceIdList = [];
            return conf;
        }
    }
})();
