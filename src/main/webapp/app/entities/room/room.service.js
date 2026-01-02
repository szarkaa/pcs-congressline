(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('Room', Room);

    Room.$inject = ['$resource'];

    function Room ($resource) {
        var resourceUrl =  'api/rooms/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongressHotelId': {
                method: 'GET',
                url: 'api/congress-hotel/:id/rooms',
                isArray: true
            },
            'queryByCongressId': {
                method: 'GET',
                url: 'api/congress/:id/rooms',
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
