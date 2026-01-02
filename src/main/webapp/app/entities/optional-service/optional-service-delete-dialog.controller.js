(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OptionalServiceDeleteController',OptionalServiceDeleteController);

    OptionalServiceDeleteController.$inject = ['$uibModalInstance', 'entity', 'OptionalService'];

    function OptionalServiceDeleteController($uibModalInstance, entity, OptionalService) {
        var vm = this;

        vm.optionalService = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            OptionalService.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
