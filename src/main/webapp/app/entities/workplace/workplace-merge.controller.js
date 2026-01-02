(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('WorkplaceMergeController', WorkplaceMergeController);

    WorkplaceMergeController.$inject = ['$scope', 'entity', 'selectedWorkplaces', 'Workplace', 'CongressSelector', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function WorkplaceMergeController ($scope, entity, selectedWorkplaces, Workplace, CongressSelector, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        //vm.dtOptions.withOption('stateSave', true);
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).notSortable()
        ];

        vm.workplace = entity;
        vm.selectedWorkplaces = selectedWorkplaces;
        vm.workplaces = [];
        vm.isAnyWorkplaceSelected = isAnyWorkplaceSelected;
        vm.toggleWorkplaceSelection = toggleWorkplaceSelection;

        function loadAllWorkplaces() {
            Workplace.queryByCongress({ id: CongressSelector.getSelectedCongress().id }, function(result) {
                vm.workplaces = result;
                for (var i = 0; i < vm.workplaces.length; i++) {
                    if (vm.workplaces[i].id === vm.workplace.id) {
                        vm.workplaces.splice(i, 1);
                        break;
                    }
                }
            });
        }

        loadAllWorkplaces();

        function isAnyWorkplaceSelected() {
            for (var prop in vm.selectedWorkplaces) {
                if (vm.selectedWorkplaces.hasOwnProperty(prop)) {
                    return true;
                }
            }
            return false;
        }

        function toggleWorkplaceSelection (workplaceId) {
        }
    }
})();
