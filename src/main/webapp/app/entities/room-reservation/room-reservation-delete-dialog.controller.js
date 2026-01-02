(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RoomReservationDeleteController',RoomReservationDeleteController);

    RoomReservationDeleteController.$inject = ['$uibModalInstance', 'entity', 'RoomReservation'];

    function RoomReservationDeleteController($uibModalInstance, entity, RoomReservation) {
        var vm = this;

        vm.rrr = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            RoomReservation.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
