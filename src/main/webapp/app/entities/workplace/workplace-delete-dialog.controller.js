(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('WorkplaceDeleteController',WorkplaceDeleteController);

    WorkplaceDeleteController.$inject = ['$uibModalInstance', 'entity', 'Workplace'];

    function WorkplaceDeleteController($uibModalInstance, entity, Workplace) {
        var vm = this;

        vm.workplace = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Workplace.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
