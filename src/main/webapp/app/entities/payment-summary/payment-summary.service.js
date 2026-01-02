(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('PaymentSummary', PaymentSummary);

    PaymentSummary.$inject = ['$resource'];

    function PaymentSummary ($resource) {
        var resourceUrl =  'api/payment-summary/:congressId';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
