(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('BankAccountDialogController', BankAccountDialogController);

    BankAccountDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'BankAccount', 'Currency', 'Congress'];

    function BankAccountDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, BankAccount, Currency, Congress) {
        var vm = this;

        vm.bankAccount = entity;
        vm.clear = clear;
        vm.save = save;
        vm.currencies = Currency.query();
        vm.congresses = Congress.query();

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.bankAccount.id !== null) {
                BankAccount.update(vm.bankAccount, onSaveSuccess, onSaveError);
            } else {
                BankAccount.save(vm.bankAccount, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:bankAccountUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
