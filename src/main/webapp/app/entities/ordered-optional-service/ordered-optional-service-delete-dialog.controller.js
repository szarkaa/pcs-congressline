(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OrderedOptionalServiceDeleteController',OrderedOptionalServiceDeleteController);

    OrderedOptionalServiceDeleteController.$inject = ['$uibModalInstance', 'entity', 'OrderedOptionalService'];

    function OrderedOptionalServiceDeleteController($uibModalInstance, entity, OrderedOptionalService) {
        var vm = this;

        vm.orderedOptionalService = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            OrderedOptionalService.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
