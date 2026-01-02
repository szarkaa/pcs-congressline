(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('PaymentTransactionReport', PaymentTransactionReport);

    PaymentTransactionReport.$inject = ['$resource', 'DateUtils'];

    function PaymentTransactionReport ($resource, DateUtils) {
        var resourceUrl =  'api/payment-transaction-report/:id';

        return $resource(resourceUrl, {}, {
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.paymentTrxDate = DateUtils.convertDateTimeFromServer(data.paymentTrxDate);
                    }
                    return data;
                }
            },
            'query': { method: 'GET', isArray: true },
            'queryPaymentRefundTransactionsByTrxId': { url: resourceUrl + '/refunds', method: 'GET', isArray: true },
            'refund': { url: resourceUrl + '/refunds', method: 'POST' }
        });
    }
})();
