(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('InvoiceReportSendToNavConfirmController',InvoiceReportSendToNavConfirmController);

    InvoiceReportSendToNavConfirmController.$inject = ['$uibModalInstance', 'entity', 'InvoiceReport'];

    function InvoiceReportSendToNavConfirmController($uibModalInstance, entity, InvoiceReport) {
        var vm = this;

        vm.invoiceId = entity.id;
        vm.clear = clear;
        vm.confirmSend = confirmSend;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmSend (id) {
            InvoiceReport.sendToNav({id: id},
                function () {
                    $uibModalInstance.close(true);
                }
            );
        }
    }
})();
