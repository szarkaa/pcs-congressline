(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('ChargedServiceDialogController', ChargedServiceDialogController);

    ChargedServiceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ChargedService', 'registrationRegistrationTypes', 'roomReservations', 'orderedOptionalServices'];

    function ChargedServiceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ChargedService, registrationRegistrationTypes, roomReservations, orderedOptionalServices) {
        var vm = this;

        vm.chargedService = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        vm.registrationRegistrationTypes = registrationRegistrationTypes;
        vm.roomReservations = roomReservations;
        vm.orderedOptionalServices = orderedOptionalServices;

        vm.setDefaultPrice = setDefaultPrice;
        vm.chargeableItemTypeOnChanged = chargeableItemTypeOnChanged;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.chargedService.paymentType == 'HOTEL') {
                vm.chargedService.chargeableItem = {
                    id: vm.chargedService.chargeableItem.id,
                    '@class': 'hu.pcs.domain.RoomReservationRegistration'
                };
            }


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

        function chargeableItemTypeOnChanged() {
            vm.chargedService.chargeableItem = null;
            vm.chargedService.amount = null;

            switch(vm.chargedService.paymentType) {
                case 'REGISTRATION':
                    if (vm.registrationRegistrationTypes.length == 1) {
                        vm.chargedService.chargeableItem = vm.registrationRegistrationTypes[0];
                        vm.setDefaultPrice();
                    }
                    break;
                case 'HOTEL':
                    if (vm.roomReservations.length == 1) {
                        vm.chargedService.chargeableItem = vm.roomReservations[0];
                        vm.setDefaultPrice();
                    }
                    break;
                case 'OPTIONAL_SERVICE':
                    if (vm.orderedOptionalServices.length == 1) {
                        vm.chargedService.chargeableItem = vm.orderedOptionalServices[0];
                        vm.setDefaultPrice();
                    }
                    break;
            }
        }

        function setDefaultPrice() {
            vm.chargedService.amount = vm.chargedService.chargeableItem && vm.chargedService.chargeableItem.chargeableItemPrice;
        }

        vm.datePickerOpenStatus.dateOfPayment = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
