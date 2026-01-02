(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RateDeleteController',RateDeleteController);

    RateDeleteController.$inject = ['$uibModalInstance', 'entity', 'Rate'];

    function RateDeleteController($uibModalInstance, entity, Rate) {
        var vm = this;

        vm.rate = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Rate.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
