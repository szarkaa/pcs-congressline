(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('OnlineRegCustomQuestion', OnlineRegCustomQuestion);

    OnlineRegCustomQuestion.$inject = ['$resource'];

    function OnlineRegCustomQuestion ($resource) {
        var resourceUrl =  '/api/congresses/online/custom-questions/:id';

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
