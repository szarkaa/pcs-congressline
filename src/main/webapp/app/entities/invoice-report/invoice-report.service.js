(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('InvoiceReport', InvoiceReport);

    InvoiceReport.$inject = ['$resource'];

    function InvoiceReport ($resource) {
        var resourceUrl =  'api/invoice-report';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryInvoiceNavStatusById': { url: 'api/invoice-report/show-nav-sending-status/:id', method: 'GET', isArray: true},
            'sendToNav': { method: 'GET', url: 'api/invoice-report/send-to-nav/:id' }
        });
    }
})();
