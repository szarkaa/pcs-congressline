(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OptionalProgramsMembersController', OptionalProgramsMembersController);

    OptionalProgramsMembersController.$inject = ['$scope', '$state', 'OptionalProgramsMembers', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector', 'OptionalService'];

    function OptionalProgramsMembersController ($scope, $state, OptionalProgramsMembers, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector, OptionalService) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
/*
            DTColumnDefBuilder.newColumnDef(6).notSortable(),
            DTColumnDefBuilder.newColumnDef(7).notSortable(),
            DTColumnDefBuilder.newColumnDef(8).notSortable()
*/
        ];

        vm.downloadReportXls = downloadReportXls;

        vm.optionalProgramsMembers = [];

        loadAll();

        function loadAll() {
            OptionalProgramsMembers.query({
                    congressId: CongressSelector.getSelectedCongress().id
                },
                function (result) {
                    vm.optionalProgramsMembers = result;
                }
            );
        }

        function downloadReportXls () {
            window.location.href = '/api/optional-program-members' +
                '/' + CongressSelector.getSelectedCongress().id.toString() + '/download-report';
        }
    }
})();
