(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RoomDialogController', RoomDialogController);

    RoomDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'congressHotel', 'Room', 'Congress', 'VatInfo', 'CongressSelector'];

    function RoomDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, congressHotel, Room, Congress, VatInfo, CongressSelector) {
        var vm = this;

        vm.room = {
            id: entity.id,
            roomType: entity.roomType,
            bed: entity.bed,
            quantity: entity.quantity,
            reserved: entity.reserved,
            price: entity.price,
            onlineVisibility: entity.onlineVisibility,
            onlineLabel: entity.onlineLabel,
            onlineExternalLink: entity.onlineExternalLink,
            onlineExternalEmail: entity.onlineExternalEmail,
            vatInfoId: entity.vatInfo ? entity.vatInfo.id : null,
            currencyId: entity.currency ? entity.currency.id : null,
            congressHotelId: $stateParams.congressHotelId
        };

        vm.vatInfos = [];
        vm.currencies = [];
        vm.congressHotel = congressHotel;
        vm.clear = clear;
        vm.save = save;
        vm.getMaxReservedRoomNumber = getMaxReservedRoomNumber;
        vm.onlineVisibilityChanged = onlineVisibilityChanged;

        Congress.get({id: CongressSelector.getSelectedCongress().id}, function(data) {
            vm.currencies = data.currencies;
        });

        VatInfo.queryForCongressAndItemType({ id: CongressSelector.getSelectedCongress().id, itemType: 'HOTEL' }, function(data) {
            vm.vatInfos = data;
        });

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function onlineVisibilityChanged() {
            if (vm.room.onlineVisibility !== 'VISIBLE') {
                vm.room.onlineLabel = null;
                vm.room.onlineExternalLink = null;
                vm.room.onlineExternalEmail = null;
            }
        }

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.room.id !== null) {
                Room.update(vm.room, onSaveSuccess, onSaveError);
            } else {
                Room.save(vm.room, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:roomUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function getMaxReservedRoomNumber () {
            var reserved = 0;
            for (var i = 0; i < vm.room.reservations && vm.room.reservations.length; i++) {
                if (vm.room.reservations[i].reserved > reserved) {
                    reserved = vm.room.reservations[i].reserved;
                }
            }
            return reserved;
        }

    }
})();
