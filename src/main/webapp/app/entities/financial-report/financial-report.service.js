(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('FinancialReport', FinancialReport);

    FinancialReport.$inject = ['$resource'];

    function FinancialReport ($resource) {
        var resourceUrl =  'api/financial-report';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
