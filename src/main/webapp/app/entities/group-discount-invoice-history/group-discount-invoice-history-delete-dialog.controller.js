(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountInvoiceHistoryDeleteController',GroupDiscountInvoiceHistoryDeleteController);

    GroupDiscountInvoiceHistoryDeleteController.$inject = ['$uibModalInstance', 'entity', 'GroupDiscountInvoiceHistory'];

    function GroupDiscountInvoiceHistoryDeleteController($uibModalInstance, entity, GroupDiscountInvoiceHistory) {
        var vm = this;

        vm.groupDiscountInvoiceHistory = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            GroupDiscountInvoiceHistory.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
