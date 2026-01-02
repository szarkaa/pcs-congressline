(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('Company', Company);

    Company.$inject = ['$resource'];

    function Company ($resource) {
        var resourceUrl =  'api/company';

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
