(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('Currency', Currency)
        .factory('CurrencyUtils', CurrencyUtils);

    Currency.$inject = ['$resource'];
    CurrencyUtils.$inject = ['$http'];

    function Currency ($resource) {
        var resourceUrl =  'api/currencies/:id';

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

    function CurrencyUtils($http) {
        return {
            isUnique: function (code) {
                return $http.get('api/currencys/unique/' + code);
            }
        };
    }

})();
