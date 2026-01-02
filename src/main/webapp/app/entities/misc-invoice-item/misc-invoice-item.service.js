(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('MiscInvoiceItem', MiscInvoiceItem);

    MiscInvoiceItem.$inject = ['$resource'];

    function MiscInvoiceItem ($resource) {
        var resourceUrl =  'api/misc-invoice-items/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
