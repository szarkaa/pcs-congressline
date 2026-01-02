(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('MiscService', MiscService);

    MiscService.$inject = ['$resource'];

    function MiscService ($resource) {
        var resourceUrl =  'api/misc-services/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongress': {
                method: 'GET',
                url: 'api/misc-services/congress/:id',
                isArray: true
            },
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
