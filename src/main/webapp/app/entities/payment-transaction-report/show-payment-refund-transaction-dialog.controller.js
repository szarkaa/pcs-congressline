(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('ShowPaymentRefundTransactionDialogController',ShowPaymentRefundTransactionDialogController);

    ShowPaymentRefundTransactionDialogController.$inject = ['$uibModalInstance', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'refunds'];

    function ShowPaymentRefundTransactionDialogController($uibModalInstance, DTOptionsBuilder, DTColumnDefBuilder, refunds) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [0, 'desc']);

        vm.refunds = refunds;
        vm.clear = clear;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
