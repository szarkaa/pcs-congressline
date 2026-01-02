(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('PayingGroupItemDeleteController',PayingGroupItemDeleteController);

    PayingGroupItemDeleteController.$inject = ['$uibModalInstance', 'entity', 'PayingGroupItem'];

    function PayingGroupItemDeleteController($uibModalInstance, entity, PayingGroupItem) {
        var vm = this;

        vm.payingGroupItem = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            PayingGroupItem.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
