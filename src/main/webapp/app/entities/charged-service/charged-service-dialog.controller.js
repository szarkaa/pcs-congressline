(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('ChargedServiceDialogController', ChargedServiceDialogController);

    ChargedServiceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ChargedService', 'registrationRegistrationTypes', 'roomReservations', 'orderedOptionalServices'];

    function ChargedServiceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ChargedService, registrationRegistrationTypes, roomReservations, orderedOptionalServices) {
        var vm = this;

        vm.chargedService = {
            id: entity.id,
            paymentMode: entity.paymentMode,
            paymentType: entity.paymentType,
            dateOfPayment: entity.dateOfPayment,
            amount: entity.amount,
            cardType: entity.cardType,
            cardNumber: entity.cardNumber,
            cardExpirationDate: entity.cardExpirationDate,
            transactionId: entity.transactionId,
            comment: entity.comment,
            chargeableItemId: entity.chargeableItemId,
            registrationId: entity.registrationId
        };
        vm.datePickerOpenStatus = {};
        vm.registrationRegistrationTypes = registrationRegistrationTypes;
        vm.roomReservations = roomReservations;
        vm.orderedOptionalServices = orderedOptionalServices;
        vm.selectedChargeableItem = null;

        vm.clear = clear;
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.paymentTypeChanged = paymentTypeChanged;
        vm.chargeableItemTypeChanged = chargeableItemTypeChanged;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.chargedService.id !== null) {
                ChargedService.update(vm.chargedService, onSaveSuccess, onSaveError);
            } else {
                ChargedService.save(vm.chargedService, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:chargedServiceUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function chargeableItemTypeChanged() {
            switch(vm.chargedService.paymentType) {
                case 'REGISTRATION':
                    for (var i = 0; i < vm.registrationRegistrationTypes.length; i++) {
                        if (vm.registrationRegistrationTypes[i].id === vm.chargedService.chargeableItemId) {
                            vm.selectedChargeableItem = vm.registrationRegistrationTypes[i];
                            setDefaultPrice();
                            break;
                        }
                    }
                    break;
                case 'HOTEL':
                    for (var i = 0; i < vm.roomReservations.length; i++) {
                        if (vm.roomReservations[i].id === vm.chargedService.chargeableItemId) {
                            vm.selectedChargeableItem = vm.roomReservations[i];
                            setDefaultPrice();
                            break;
                        }
                    }
                    break;
                case 'OPTIONAL_SERVICE':
                    for (var i = 0; i < vm.orderedOptionalServices.length; i++) {
                        if (vm.orderedOptionalServices[i].id === vm.chargedService.chargeableItemId) {
                            vm.selectedChargeableItem = vm.orderedOptionalServices[i];
                            setDefaultPrice();
                            break;
                        }
                    }
                    break;
            }
        }

        function paymentTypeChanged() {
            vm.chargedService.chargeableItemId = null;
            vm.chargedService.amount = null;

            switch(vm.chargedService.paymentType) {
                case 'REGISTRATION':
                    if (vm.registrationRegistrationTypes.length === 1) {
                        vm.selectedChargeableItem = vm.registrationRegistrationTypes[0];
                        vm.chargedService.chargeableItemId = vm.selectedChargeableItem.id;
                        vm.chargeableItemTypeChanged();
                    }
                    break;
                case 'HOTEL':
                    if (vm.roomReservations.length === 1) {
                        vm.selectedChargeableItem = vm.roomReservations[0];
                        vm.chargedService.chargeableItemId = vm.selectedChargeableItem.id;
                        vm.chargeableItemTypeChanged();
                    }
                    break;
                case 'OPTIONAL_SERVICE':
                    if (vm.orderedOptionalServices.length === 1) {
                        vm.selectedChargeableItem = vm.orderedOptionalServices[0];
                        vm.chargedService.chargeableItemId = vm.selectedChargeableItem.id;
                        vm.chargeableItemTypeChanged();
                    }
                    break;
            }
        }

        function setDefaultPrice() {
            vm.chargedService.amount = vm.chargedService.chargeableItemId && vm.selectedChargeableItem.chargeableItemPrice;
        }

        vm.datePickerOpenStatus.dateOfPayment = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
