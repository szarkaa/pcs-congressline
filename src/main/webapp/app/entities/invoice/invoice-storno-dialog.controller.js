(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('InvoiceStornoController', InvoiceStornoController);

    InvoiceStornoController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'entity', 'Invoice'];

    function InvoiceStornoController ($timeout, $scope, $uibModalInstance, entity, Invoice) {
        var vm = this;
        vm.invoice = entity;
        vm.clear = clear;
        vm.confirmStorno = confirmStorno;

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmStorno() {
            Invoice.storno({id: vm.invoice.id},
                function (result) {
                    var pdfLink = '/api/invoices/' + result.id + '/pdf';
                    window.open(pdfLink, '_blank');
                    $uibModalInstance.close(true);
                });
        }
    }
})();