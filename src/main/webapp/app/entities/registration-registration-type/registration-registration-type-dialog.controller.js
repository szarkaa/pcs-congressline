(function() {
    'use strict';

    angular.module('pcsApp')
        .controller('RegistrationRegistrationTypeDialogController', RegistrationRegistrationTypeDialogController);

    RegistrationRegistrationTypeDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'entity', 'registrationCurrency', 'RegistrationRegistrationType', 'RegistrationType', 'PayingGroupItem', 'CongressSelector'];

    function RegistrationRegistrationTypeDialogController ($timeout, $scope, $uibModalInstance, entity, registrationCurrency, RegistrationRegistrationType, RegistrationType, PayingGroupItem, CongressSelector) {
        var vm = this;

        vm.registrationRegistrationType = {
            id: entity.id,
            regFee: entity.chargeableItemPrice,
            currency: entity.chargeableItemCurrency,
            createdDate: new Date(),
            accPeople: entity.accPeople,
            registrationTypeId: entity.registrationTypeId,
            payingGroupItemId: entity.payingGroupItemId,
            registrationId: entity.registrationId
        };
        vm.selectedRegistrationType = null;
        vm.datePickerOpenStatus = {};
        vm.registrationTypes = RegistrationType.queryByCongress({id: CongressSelector.getSelectedCongress().id});
        vm.payingGroupItems = PayingGroupItem.queryByCongressAndItemType({id: CongressSelector.getSelectedCongress().id, itemType: 'REGISTRATION'});
        vm.registrationCurrency = registrationCurrency;

        vm.clear = clear;
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.registrationTypeSelectionChanged = registrationTypeSelectionChanged;

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        $scope.$watchCollection(
            () => vm.registrationTypes,
            function (newVal) {
                if (!newVal || !newVal.length) return;

                vm.registrationTypeSelectionChanged();
            }
        );

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
            if (vm.registrationRegistrationType.registrationTypeId) {
                RegistrationRegistrationType.queryRegFeeByRegistrationTypeId({
                    registrationId: vm.registrationRegistrationType.registrationId,
                    registrationTypeId: vm.registrationRegistrationType.registrationTypeId
                }, function (result) {
                    vm.registrationRegistrationType.regFee = result.regFee;
                    vm.registrationRegistrationType.currency = result.currency;
                });
            }
        }

        function registrationTypeSelectionChanged() {
            setRegFeeAndCurrency();
            for (var i = 0; i < vm.registrationTypes.length; i++) {
                if (vm.registrationTypes[i].id === vm.registrationRegistrationType.registrationTypeId) {
                    vm.selectedRegistrationType = vm.registrationTypes[i];
                    break;
                }
            }
        }

        vm.datePickerOpenStatus.createdDate = false;
        vm.datePickerOpenStatus.dateOfGroupPayment = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
