(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('SendInvoiceEmailDialogController', SendInvoiceEmailDialogController);

    SendInvoiceEmailDialogController.$inject = ['$timeout', '$scope', 'invoiceEmail', '$uibModalInstance', 'Invoice'];

    function SendInvoiceEmailDialogController ($timeout, $scope, invoiceEmail, $uibModalInstance, Invoice) {
        var vm = this;

        vm.invoiceEmail = { invoice: invoiceEmail.invoice, email: invoiceEmail.email };

        vm.clear = clear;
        vm.send = send;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function send () {
            vm.isSaving = true;
            Invoice.sendEmail({ invoiceId: vm.invoiceEmail.invoice, email: vm.invoiceEmail.email }, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:emailSent', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
