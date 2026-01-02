(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountPaymentDeleteController',GroupDiscountPaymentDeleteController);

    GroupDiscountPaymentDeleteController.$inject = ['$uibModalInstance', 'entity', 'GroupDiscountPayment'];

    function GroupDiscountPaymentDeleteController($uibModalInstance, entity, GroupDiscountPayment) {
        var vm = this;

        vm.groupDiscountPayment = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            GroupDiscountPayment.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
