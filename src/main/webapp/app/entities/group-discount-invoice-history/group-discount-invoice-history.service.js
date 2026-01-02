(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('GroupDiscountInvoiceHistory', GroupDiscountInvoiceHistory);

    GroupDiscountInvoiceHistory.$inject = ['$resource'];

    function GroupDiscountInvoiceHistory ($resource) {
        var resourceUrl =  'api/group-discount-invoice-histories/:id';

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
