(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('ChargedServiceDeleteController',ChargedServiceDeleteController);

    ChargedServiceDeleteController.$inject = ['$uibModalInstance', 'entity', 'ChargedService'];

    function ChargedServiceDeleteController($uibModalInstance, entity, ChargedService) {
        var vm = this;

        vm.chargedService = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ChargedService.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
