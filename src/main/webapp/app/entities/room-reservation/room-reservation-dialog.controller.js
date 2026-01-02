(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RoomReservationDialogController', RoomReservationDialogController);

    RoomReservationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity',
        'RoomReservation', 'Room', 'PayingGroupItem', 'CongressSelector', 'registrationCurrency', 'invoicedChargeableItemIds'];

    function RoomReservationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity,
        RoomReservation, Room, PayingGroupItem, CongressSelector, registrationCurrency, invoicedChargeableItemIds) {
        var vm = this;

        vm.roomReservation = entity;
        vm.invoicedChargeableItemIds = invoicedChargeableItemIds;
        vm.clear = clear;
        vm.save = save;
        vm.filterChargeableItemsByCurrencyCriteria = filterChargeableItemsByCurrencyCriteria;
        vm.isDateEditable = isDateEditable;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.rooms = Room.queryByCongressId({id: CongressSelector.getSelectedCongress().id});
        vm.payingGroupItems = PayingGroupItem.queryByCongressAndItemType({id: CongressSelector.getSelectedCongress().id, itemType: 'HOTEL'});
        vm.registrationCurrency = registrationCurrency;


        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.roomReservation.id !== null) {
                RoomReservation.update(vm.roomReservation, onSaveSuccess, onSaveError);
            } else {
                RoomReservation.save(vm.roomReservation, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:roomReservationUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.arrivalDate = false;
        vm.datePickerOpenStatus.departureDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function filterChargeableItemsByCurrencyCriteria(obj) {
            var re = new RegExp(this.searchText, 'i');
            return (!this.registrationCurrency || this.registrationCurrency === obj.currency.currency) &&
                (!this.searchText || re.test(obj.congressHotel.hotel.name) ||  re.test(obj.roomType));
        }

        function isDateEditable () {
            var editable = true;
            for (var i = 0; i < vm.invoicedChargeableItemIds.length; i++) {
                if (vm.invoicedChargeableItemIds[i] == vm.roomReservation.id) {
                    editable = false;
                    break;
                }
            }
            return editable;
        }
    }
})();
