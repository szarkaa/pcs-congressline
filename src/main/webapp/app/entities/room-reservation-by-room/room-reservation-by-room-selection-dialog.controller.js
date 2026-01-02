 (function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RoomReservationByRoomSelectionDialogController', RoomReservationByRoomSelectionDialogController);

    RoomReservationByRoomSelectionDialogController.$inject = ['$timeout', '$state', '$scope', '$stateParams', '$uibModalInstance', 'listFilter', 'CongressHotel', 'CongressSelector', 'RoomReservationByRoomFilter'];

    function RoomReservationByRoomSelectionDialogController ($timeout, $state, $scope, $stateParams, $uibModalInstance, listFilter, CongressHotel, CongressSelector, RoomReservationByRoomFilter) {
        var vm = this;

        vm.listFilter = listFilter;
        vm.clear = clear;
        vm.select = select;
        CongressHotel.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.congressHotels = result;
        });

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
            $state.go('room-reservation-by-room');
        }

        function select () {
            vm.isSaving = true;
            RoomReservationByRoomFilter.setRoomReservationByRoomFilter(vm.listFilter);
            $uibModalInstance.close();
            $state.go('room-reservation-by-room', null, {reload: 'room-reservation-by-room'});
            vm.isSaving = false;
        }

    }
})();
