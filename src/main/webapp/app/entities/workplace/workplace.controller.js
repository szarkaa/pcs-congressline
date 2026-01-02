(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('WorkplaceController', WorkplaceController);

    WorkplaceController.$inject = ['$scope', '$state', 'Workplace', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector', 'isPartner'];

    function WorkplaceController ($scope, $state, Workplace, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector, isPartner) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions()
            .withDOM('<"html5buttons"B>ltfgitp')
            .withButtons([
                {extend: 'excel', title: 'workplaces'},
                {extend: 'pdf', title: 'workplaces'}
            ]);

        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.workplaces = [];
        vm.isPartner = isPartner;

        loadAll();

        function loadAll() {
            if (!isPartner) {
                Workplace.queryByCongress({ id: CongressSelector.getSelectedCongress().id }, function(result) {
                    vm.workplaces = result;
                });
            }
            else {
                Workplace.queryByCongress({ id: 0 }, function(result) {
                    vm.workplaces = result;
                });
            }

        }
    }
})();
