(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('PayingGroupItemController', PayingGroupItemController);

    PayingGroupItemController.$inject = ['$scope', '$state', 'PayingGroupItem', 'payingGroup', 'payingGroupItems', 'DTOptionsBuilder', 'DTColumnBuilder', 'DTColumnDefBuilder'];

    function PayingGroupItemController ($scope, $state, PayingGroupItem, payingGroup, payingGroupItems, DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.payingGroup = payingGroup;
        vm.payingGroupItems = payingGroupItems;

        vm.payingGroupItems = [];

        loadAll();

        function loadAll() {
            PayingGroupItem.queryByPayingGroup({id: vm.payingGroup.id}, function(result) {
                vm.payingGroupItems = result;
            });
        }
    }
})();
