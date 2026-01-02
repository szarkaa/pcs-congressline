 (function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountItemSelectionDialogController', GroupDiscountItemSelectionDialogController);

    GroupDiscountItemSelectionDialogController.$inject = ['$timeout', '$state', 'CongressSelector', '$uibModalInstance', 'groupDiscountItemFilter', 'PayingGroup', 'GroupDiscountItemFilter'];

    function GroupDiscountItemSelectionDialogController ($timeout, $state, CongressSelector, $uibModalInstance, groupDiscountItemFilter, PayingGroup, GroupDiscountItemFilter) {
        var vm = this;

        vm.clear = clear;
        vm.select = select;
        vm.payingGroups = PayingGroup.queryByCongress({id: CongressSelector.getSelectedCongress().id});
        vm.groupDiscountItemFilter = groupDiscountItemFilter;

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
            $state.go('group-discount-item');
        }

        function select () {
            vm.isSaving = true;
            vm.groupDiscountItemFilter.selectedChargeableItemIds = {};
            GroupDiscountItemFilter.setGroupDiscountItemFilter(vm.groupDiscountItemFilter);
            $uibModalInstance.close();
            $state.go('group-discount-item', null, {reload: 'group-discount-item'});
            vm.isSaving = false;
        }

    }
})();
