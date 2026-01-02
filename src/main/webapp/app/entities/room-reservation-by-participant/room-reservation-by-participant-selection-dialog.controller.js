 (function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RoomReservationByParticipantSelectionDialogController', RoomReservationByParticipantSelectionDialogController);

    RoomReservationByParticipantSelectionDialogController.$inject = ['$timeout', '$state', '$scope', '$stateParams', '$uibModalInstance', 'listFilter', 'CongressHotel', 'CongressSelector', 'RoomReservationByParticipantFilter'];

    function RoomReservationByParticipantSelectionDialogController ($timeout, $state, $scope, $stateParams, $uibModalInstance, listFilter, CongressHotel, CongressSelector, RoomReservationByParticipantFilter) {
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
            $state.go('room-reservation-by-participant');
        }

        function select () {
            vm.isSaving = true;
            RoomReservationByParticipantFilter.setRoomReservationByParticipantFilter(vm.listFilter);
            $uibModalInstance.close();
            $state.go('room-reservation-by-participant', null, {reload: 'room-reservation-by-participant'});
            vm.isSaving = false;
        }

    }
})();
