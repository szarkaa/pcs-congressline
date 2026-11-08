(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('FinancialReportController', FinancialReportController);

    FinancialReportController.$inject = ['$scope', '$state', 'reportFilter', 'FinancialReport', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'B64Encoder', 'Confirmation'];

    function FinancialReportController ($scope, $state, reportFilter, FinancialReport, DTOptionsBuilder, DTColumnDefBuilder, B64Encoder, Confirmation) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [3, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtOptions.withOption('scrollY', '500px');
        vm.dtOptions.withOption('scrollX', true);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).notSortable(),
            DTColumnDefBuilder.newColumnDef(1).notSortable()
        ];

        vm.reportFilter = reportFilter;
        vm.isFiltered = false;
        vm.selectedRegIds = {};
        vm.allRegIdSelected = false;
        vm.downloadReportXls = downloadReportXls;
        vm.printConfirmation = printConfirmation;
        vm.toggleRegIdSelection = toggleRegIdSelection;
        vm.toggleAllRegIdSelection = toggleAllRegIdSelection;
        vm.isAnyRegIdSelected = isAnyRegIdSelected;
        vm.getSelectedRegIds = getSelectedRegIds;

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
                    clearRegIdSelection();
                }
            );
        }

        function buildReportFilter(reportFilter) {
            return {
                participantsToPay: reportFilter.participantsToPay,
                congressId: reportFilter.congressId,
                registrationIds: []
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

        function toggleRegIdSelection(regId) {
            // vm.selectedRegIds[regId.toString()] = !isOnlineRegIdSelected(regId);
        }

        function getSelectedRegIds() {
            var regIdList = [];
            for (var prop in vm.selectedRegIds) {
                if (vm.selectedRegIds.hasOwnProperty(prop) && vm.selectedRegIds[prop]) {
                    regIdList.push(parseInt(prop, 10));
                }
            }
            return regIdList;
        }

        function clearRegIdSelection() {
            vm.selectedRegIds = {};
            vm.allRegIdSelected = false;
        }

        function toggleAllRegIdSelection() {
            if (!vm.allRegIdSelected) {
                vm.selectedRegIds = {};
            }
            else {
                vm.selectedRegIds = {};
                for (var i = 0; i < vm.financialReports.length; i++) {
                    vm.selectedRegIds[vm.financialReports[i].id.toString()] = true;
                }
            }
        }

        function isRegIdSelected(regId) {
            for (var prop in vm.selectedRegIds) {
                if (vm.selectedRegIds.hasOwnProperty(prop) && prop === regId) {
                    return true;
                }
            }
            return false;
        }

        function isAnyRegIdSelected() {
            for (var prop in vm.selectedRegIds) {
                if (vm.selectedRegIds.hasOwnProperty(prop) && vm.selectedRegIds[prop]) {
                    return true;
                }
            }
            return false;
        }
    }
})();
