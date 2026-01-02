(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('CongressHotel', CongressHotel);

    CongressHotel.$inject = ['$resource'];

    function CongressHotel ($resource) {
        var resourceUrl =  'api/congress-hotels/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongress': {
                method: 'GET',
                url: 'api/congress-hotels/congress/:id',
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
