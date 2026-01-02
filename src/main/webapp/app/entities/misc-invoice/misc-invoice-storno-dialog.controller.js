(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscInvoiceStornoController', MiscInvoiceStornoController);

    MiscInvoiceStornoController.$inject = ['$scope', '$uibModalInstance', 'entity', 'MiscInvoice'];

    function MiscInvoiceStornoController ($scope, $uibModalInstance, entity, MiscInvoice) {
        var vm = this;

        vm.invoiceCongress = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.confirmStorno = confirmStorno;

        function confirmStorno() {
            MiscInvoice.storno({id: vm.invoiceCongress.invoice.id},
                function (result) {
                    var pdfLink = '/api/misc-invoices/' + result.id + '/pdf';
                    window.open(pdfLink, '_blank');
                    $uibModalInstance.close(true);
                });
        }

    }
})();
