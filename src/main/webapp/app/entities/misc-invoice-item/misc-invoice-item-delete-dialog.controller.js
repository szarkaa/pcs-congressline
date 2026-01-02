(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscInvoiceItemDeleteController',MiscInvoiceItemDeleteController);

    MiscInvoiceItemDeleteController.$inject = ['$uibModalInstance', 'entity', 'miscInvoice'];

    function MiscInvoiceItemDeleteController($uibModalInstance, entity, miscInvoice) {
        var vm = this;

        vm.miscInvoiceItem = entity;
        vm.miscInvoice = miscInvoice;

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.confirmDelete = function (id) {
            for (var i = 0; i < vm.miscInvoice.miscInvoiceItems.length; i++) {
                if (vm.miscInvoice.miscInvoiceItems[i].id == id) {
                    vm.miscInvoice.miscInvoiceItems.splice(i, 1);
                }
            }
            $uibModalInstance.close(true);
        };
    }
})();
