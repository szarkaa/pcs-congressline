(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('PayingGroupItemDialogController', PayingGroupItemDialogController);

    PayingGroupItemDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'PayingGroupItem', 'PayingGroup'];

    function PayingGroupItemDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, PayingGroupItem, PayingGroup) {
        var vm = this;

        vm.payingGroupItem = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.clearOtherFields = clearOtherFields;
        vm.isHotelDateDiscountVisible = isHotelDateDiscountVisible;
        vm.isFieldRequired = isFieldRequired;
        vm.payinggroups = PayingGroup.query();

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clearOtherFields(fieldType) {
            if ('VALUE' === fieldType) {
                vm.payingGroupItem.amountPercentage = null;
                vm.payingGroupItem.hotelDateFrom = null;
                vm.payingGroupItem.hotelDateTo = null;
            }
            else if ('PERCENTAGE' === fieldType) {
                vm.payingGroupItem.amountValue = null;
                vm.payingGroupItem.hotelDateFrom = null;
                vm.payingGroupItem.hotelDateTo = null;
            }
            else if ('HOTEL_DATE' === fieldType) {
                vm.payingGroupItem.amountValue = null;
                vm.payingGroupItem.amountPercentage = null;
            }
        }

        function isHotelDateDiscountVisible () {
            if (vm.payingGroupItem.chargeableItemType === 'HOTEL') {
                return true;
            }
            else {
                return false;
            }
        }

        function isFieldRequired () {
            if (vm.payingGroupItem.chargeableItemType === 'HOTEL') {
                return !vm.payingGroupItem.amountValue && !vm.payingGroupItem.amountPercentage && !(vm.payingGroupItem.hotelDateFrom || vm.payingGroupItem.hotelDateTo);
            }
            else {
                return !vm.payingGroupItem.amountValue && !vm.payingGroupItem.amountPercentage;
            }
        }

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.payingGroupItem.id !== null) {
                PayingGroupItem.update(vm.payingGroupItem, onSaveSuccess, onSaveError);
            } else {
                PayingGroupItem.save(vm.payingGroupItem, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:payingGroupItemUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.hotelDateFrom = false;
        vm.datePickerOpenStatus.hotelDateTo = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
