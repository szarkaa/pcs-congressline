(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RoomDialogController', RoomDialogController);

    RoomDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'congressHotel', 'Room', 'Congress', 'VatInfo', 'CongressSelector'];

    function RoomDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, congressHotel, Room, Congress, VatInfo, CongressSelector) {
        var vm = this;

        vm.room = entity;
        vm.congressHotel = congressHotel;
        vm.clear = clear;
        vm.save = save;
        vm.currencies = [];
        vm.vatInfos = [];
        vm.getMaxReservedRoomNumber = getMaxReservedRoomNumber;

        Congress.get({id: CongressSelector.getSelectedCongress().id}, function(data) {
            vm.currencies = data.currencies;
        });

        VatInfo.queryForCongressAndItemType({ id: CongressSelector.getSelectedCongress().id, itemType: 'HOTEL' }, function(data) {
            vm.vatInfos = data;
        });

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function createRoomEntity() {
            return {
                id: vm.room.id,
                congressHotel: {id: vm.congressHotel.id},
                roomType: vm.room.roomType,
                bed: vm.room.bed,
                quantity: vm.room.quantity,
                price: vm.room.price,
                currency: {id: vm.room.currency.id},
                vatInfo: {id: vm.room.vatInfo.id},
                onlineVisibility: vm.room.onlineVisibility,
                onlineLabel: vm.room.onlineLabel,
                onlineExternalLink: vm.room.onlineExternalLink
            };
        }

        function save () {
            vm.isSaving = true;
            if (vm.room.id !== null) {
                Room.update(createRoomEntity(), onSaveSuccess, onSaveError);
            } else {
                Room.save(createRoomEntity(), onSaveSuccess, onSaveError);
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
