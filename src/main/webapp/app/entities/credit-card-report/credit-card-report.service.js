(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('CreditCardReport', CreditCardReport);

    CreditCardReport.$inject = ['$resource'];

    function CreditCardReport ($resource) {
        var resourceUrl =  'api/credit-card-report';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET',isArray: true}
        });
    }
})();
