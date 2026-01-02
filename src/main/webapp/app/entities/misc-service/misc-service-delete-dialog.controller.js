(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscServiceDeleteController',MiscServiceDeleteController);

    MiscServiceDeleteController.$inject = ['$uibModalInstance', 'entity', 'MiscService'];

    function MiscServiceDeleteController($uibModalInstance, entity, MiscService) {
        var vm = this;

        vm.miscService = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            MiscService.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
