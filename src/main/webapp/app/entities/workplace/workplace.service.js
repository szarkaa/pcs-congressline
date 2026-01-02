(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('Workplace', Workplace);

    Workplace.$inject = ['$resource'];

    function Workplace ($resource) {
        var resourceUrl =  'api/workplaces/:id';

        return $resource(resourceUrl, {}, {
            'queryByCongress': {
                method: 'GET',
                url: 'api/workplaces/congress/:id',
                isArray: true
            },
            'queryForCongress': {
                method: 'GET',
                url: 'api/workplaces/all/congress/:id'
                ,isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' },
            'merge': {
                method: 'POST',
                url: 'api/workplaces/merge',
                transformRequest: function (data) {
                    return angular.toJson(data);
                }
            }
        });
    }
})();
