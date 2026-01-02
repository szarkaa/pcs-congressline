(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('CongressHotelDeleteController',CongressHotelDeleteController);

    CongressHotelDeleteController.$inject = ['$uibModalInstance', 'entity', 'CongressHotel'];

    function CongressHotelDeleteController($uibModalInstance, entity, CongressHotel) {
        var vm = this;

        vm.congressHotel = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            CongressHotel.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
