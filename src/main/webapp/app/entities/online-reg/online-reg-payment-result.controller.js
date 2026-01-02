(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegPaymentResultController', OnlineRegPaymentResultController);

    OnlineRegPaymentResultController.$inject = ['$timeout', '$state', '$stateParams', 'OnlineReg', 'paymentResult'];

    function OnlineRegPaymentResultController ($timeout, $state, $stateParams, OnlineReg, paymentResult) {
        var vm = this;
        vm.paymentResult = paymentResult;

        vm.headerStyle = headerStyle;
        vm.getErrorStatus = getErrorStatus;

        setStripePaymentStatus();

        function headerStyle() {
            return vm.paymentResult && vm.paymentResult.colorCode ? { 'color': '#ffffff', 'background-color': vm.paymentResult.colorCode } : {};
        }

        function setStripePaymentStatus() {
            OnlineReg.setStripePaymentStatus({txId: $stateParams.txid});
        }

        function getErrorStatus() {
            if (vm.paymentResult.paymentTrxResultCode === '0') {
                if (vm.paymentResult.paymentTrxStatus === 'PAYMENT_CANCELLED') {
                    return 'paymentCancelled';
                }
                else if (vm.paymentResult.paymentTrxStatus === 'PAYMENT_DENIED') {
                    if (vm.paymentResult.paymentTrxResultMessage === 'Session expired') {
                        return 'sessionExpired';
                    }
                    else {
                        return 'paymentDenied';
                    }
                }
                else {
                    return 'generalError';
                }
            }
            else if (vm.paymentResult.paymentTrxResultCode === '120') {
                return 'merchantBlocked';
            }
            else if (vm.paymentResult.paymentTrxResultCode === '130') {
                return 'sessionExpired';
            }
            else if (vm.paymentResult.paymentTrxResultCode === '140') {
                return 'paymentNotFound';
            }
            else {
                return 'generalError';
            }
        }

    }
})();
