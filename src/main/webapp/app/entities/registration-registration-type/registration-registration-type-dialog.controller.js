(function() {
    'use strict';

    angular.module('pcsApp')
        .controller('RegistrationRegistrationTypeDialogController', RegistrationRegistrationTypeDialogController);

    RegistrationRegistrationTypeDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'entity', 'registrationCurrency', 'RegistrationRegistrationType', 'RegistrationType', 'PayingGroupItem', 'CongressSelector'];

    function RegistrationRegistrationTypeDialogController ($timeout, $scope, $uibModalInstance, entity, registrationCurrency, RegistrationRegistrationType, RegistrationType, PayingGroupItem, CongressSelector) {
        var vm = this;

        vm.registrationRegistrationType = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.setRegFeeAndCurrency = setRegFeeAndCurrency;
        vm.registrationTypes = RegistrationType.queryByCongress({id: CongressSelector.getSelectedCongress().id});
        vm.payingGroupItems = PayingGroupItem.queryByCongressAndItemType({id: CongressSelector.getSelectedCongress().id, itemType: 'REGISTRATION'});
        vm.registrationCurrency = registrationCurrency;

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.registrationRegistrationType.id !== null) {
                RegistrationRegistrationType.update(vm.registrationRegistrationType, onSaveSuccess, onSaveError);
            } else {
                RegistrationRegistrationType.save(vm.registrationRegistrationType, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:registrationRegistrationTypeUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function setRegFeeAndCurrency() {
            RegistrationRegistrationType.queryRegFeeVMByRegistrationTypeId({registrationId: vm.registrationRegistrationType.registration.id, registrationTypeId: vm.registrationRegistrationType.registrationType.id}, function (result) {
                vm.registrationRegistrationType.regFee = result.regFee;
                vm.registrationRegistrationType.currency = result.currency;
            });
        }


        vm.datePickerOpenStatus.createdDate = false;
        vm.datePickerOpenStatus.dateOfGroupPayment = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
