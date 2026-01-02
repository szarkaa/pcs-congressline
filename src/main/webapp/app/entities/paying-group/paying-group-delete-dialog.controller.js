(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('PayingGroupDeleteController',PayingGroupDeleteController);

    PayingGroupDeleteController.$inject = ['$uibModalInstance', 'entity', 'PayingGroup'];

    function PayingGroupDeleteController($uibModalInstance, entity, PayingGroup) {
        var vm = this;

        vm.payingGroup = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            PayingGroup.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
