(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscInvoiceStornoController', MiscInvoiceStornoController);

    MiscInvoiceStornoController.$inject = ['$scope', '$uibModalInstance', 'entity', 'MiscInvoice'];

    function MiscInvoiceStornoController ($scope, $uibModalInstance, entity, MiscInvoice) {
        var vm = this;

        vm.miscInvoice = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.confirmStorno = confirmStorno;

        function confirmStorno() {
            MiscInvoice.storno({id: vm.miscInvoice.invoiceId},
                function (result) {
                    var pdfLink = '/api/misc-invoices/' + result.invoiceCongressId + '/pdf';
                    window.open(pdfLink, '_blank');
                    $uibModalInstance.close(true);
                });
        }

    }
})();
