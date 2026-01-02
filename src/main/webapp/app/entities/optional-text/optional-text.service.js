(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('OptionalText', OptionalText);

    OptionalText.$inject = ['$resource'];

    function OptionalText ($resource) {
        var resourceUrl =  'api/optional-texts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongress': {
                method: 'GET',
                url: 'api/optional-texts/congress/:id',
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
