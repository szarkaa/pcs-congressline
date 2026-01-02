(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OptionalServiceApplicantController', OptionalServiceApplicantController);

    OptionalServiceApplicantController.$inject = ['$scope', '$state', 'OptionalServiceApplicant', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector', 'OptionalService', 'listFilter'];

    function OptionalServiceApplicantController ($scope, $state, OptionalServiceApplicant, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector, OptionalService, listFilter) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [];

        OptionalService.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.optionalServices = result;
        });

        vm.listFilter = listFilter;
        vm.downloadReportXls = downloadReportXls;

        vm.optionalServiceApplicants = [];

        loadAll();

        function loadAll() {
            if (vm.listFilter.optionalService) {
                OptionalServiceApplicant.query({
                        optionalServiceId: listFilter.optionalService.id
                    },
                    function (result) {
                        vm.optionalServiceApplicants = result;
                    }
                );
            }
        }

        function downloadReportXls () {
            window.location.href = '/api/optional-service-applicants' +
                '/' + listFilter.optionalService.id.toString() + '/download-report';
        }
    }
})();
