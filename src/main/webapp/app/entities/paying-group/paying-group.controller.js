(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('PayingGroupController', PayingGroupController);

    PayingGroupController.$inject = ['$scope', '$state', 'PayingGroup', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector'];

    function PayingGroupController ($scope, $state, PayingGroup, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector) {
        var vm = this;
        $scope.dtOptions = DTOptionsBuilder.newOptions();
        $scope.dtOptions.withOption('stateSave', true);
        $scope.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.payingGroups = [];

        loadAll();

        function loadAll() {
            PayingGroup.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                vm.payingGroups = result;
            });
        }
    }
})();
