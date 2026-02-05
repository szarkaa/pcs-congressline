(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RegFeeDetailsByParticipantController', RegFeeDetailsByParticipantController);

    RegFeeDetailsByParticipantController.$inject = ['$scope', '$state', 'RegFeeDetailsByParticipant', 'RegistrationType', 'reportFilter',
        'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector', 'B64Encoder'];

    function RegFeeDetailsByParticipantController ($scope, $state, RegFeeDetailsByParticipant, RegistrationType, reportFilter,
       DTOptionsBuilder, DTColumnDefBuilder, CongressSelector, B64Encoder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [];

        vm.registrationTypes = RegistrationType.queryByCongress({id: CongressSelector.getSelectedCongress().id});
        vm.reportFilter = reportFilter;

        vm.search = search;
        vm.clear = clear;
        vm.isAnyFilterSet = isAnyFilterSet;
        vm.downloadReportXls = downloadReportXls;

        vm.reportList = [];

        function clear() {
            vm.reportList = [];
            vm.reportFilter.registrationTypeId = null;
        }

        function buildReportFilter(reportFilter) {
            return {
                congressId: CongressSelector.getSelectedCongress().id,
                registrationTypeId: reportFilter.regType.id
            };
        }

        function search() {
            RegFeeDetailsByParticipant.query({
                    query: B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)))
                },
                function (result) {
                    vm.reportList = result;
                }
            );
        }

        function isAnyFilterSet() {
            return vm.reportFilter.regType;
        }

        function downloadReportXls () {
            window.location.href = '/api/regfee-details-by-participant/download-report?query=' + B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)));
        }
    }
})();
