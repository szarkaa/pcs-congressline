(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegDiscountCodeDialogController', OnlineRegDiscountCodeDialogController);

    OnlineRegDiscountCodeDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'entity', 'OnlineRegDiscountCode'];

    function OnlineRegDiscountCodeDialogController ($timeout, $scope, $uibModalInstance, entity, OnlineRegDiscountCode) {
        var vm = this;

        vm.discountCode = entity;
        vm.clear = clear;
        vm.save = save;
        vm.onDiscountTypeChanged = onDiscountTypeChanged;

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.discountCode.id !== null) {
                OnlineRegDiscountCode.update(vm.discountCode, onSaveSuccess, onSaveError);
            } else {
                OnlineRegDiscountCode.save(vm.discountCode, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:onlineRegDiscountCodeUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function onDiscountTypeChanged() {
            if (vm.discountCode.discountType === '') {
                vm.discountCode.discountType = null;
            }
        }

    }
})();
