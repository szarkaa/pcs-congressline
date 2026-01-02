(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('PaymentTransactionRefundDialogController',PaymentTransactionRefundDialogController);

    PaymentTransactionRefundDialogController.$inject = ['$uibModalInstance', 'entity', 'PaymentTransactionReport'];

    function PaymentTransactionRefundDialogController($uibModalInstance, entity, PaymentTransactionReport) {
        var vm = this;

        vm.paymentTransaction = entity;
        vm.refundAmount = vm.paymentTransaction.amount;
        vm.clear = clear;
        vm.confirm = confirm;
        vm.isReverse = isReverse;

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function confirm() {
            PaymentTransactionReport.refund({
                    txId: vm.paymentTransaction.transactionId,
                    amount: vm.refundAmount,
                    currency: vm.paymentTransaction.currency
                },
                function (result) {
                    $uibModalInstance.close(true);
                }
            );
        }

        function isReverse() {
            return vm.paymentTransaction.paymentTrxStatus === 'PAYMENT_WAITING_FOR_SETTLEMENT'
        }
    }

})();
