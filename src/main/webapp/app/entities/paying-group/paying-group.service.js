(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('PayingGroup', PayingGroup);

    PayingGroup.$inject = ['$resource'];

    function PayingGroup ($resource) {
        var resourceUrl =  'api/paying-groups/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongress': {
                method: 'GET',
                url: 'api/paying-groups/congress/:id',
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
