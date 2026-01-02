(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('VatInfoDialogController', VatInfoDialogController);

    VatInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'VatInfo', 'Congress'];

    function VatInfoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, VatInfo, Congress) {
        var vm = this;

        vm.vatInfo = entity;
        vm.clear = clear;
        vm.save = save;
        vm.vatRateTypeChanged = vatRateTypeChanged;


        vm.congresses = Congress.query();

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.vatInfo.id !== null) {
                VatInfo.update(vm.vatInfo, onSaveSuccess, onSaveError);
            } else {
                VatInfo.save(vm.vatInfo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:vatInfoUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function vatRateTypeChanged() {
            if (vm.vatInfo.vatRateType !== 'REGULAR') {
                vm.vatInfo.vat = 0;
            }
            else {
                vm.vatInfo.vatExceptionReason = null;
            }
        }


    }
})();
