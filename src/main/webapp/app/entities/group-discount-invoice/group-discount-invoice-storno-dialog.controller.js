(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountInvoiceStornoController',GroupDiscountInvoiceStornoController);

    GroupDiscountInvoiceStornoController.$inject = ['$uibModalInstance', 'entity', 'GroupDiscountInvoice'];

    function GroupDiscountInvoiceStornoController($uibModalInstance, entity, GroupDiscountInvoice) {
        var vm = this;

        vm.groupDiscountInvoice = entity;
        vm.clear = clear;
        vm.confirmStorno = confirmStorno;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmStorno () {
            GroupDiscountInvoice.storno({id: vm.groupDiscountInvoice.id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
