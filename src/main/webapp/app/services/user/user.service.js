(function () {
    'use strict';

    angular
        .module('pcsApp')
        .factory('User', User);

    User.$inject = ['$resource', 'DateUtils'];

    function User ($resource, DateUtils) {
        var service = $resource('api/users/:login', {}, {
            'query': {method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    for (var i = 0; i < data.congresses.length; i++) {
                        data.congresses[i].startDate = DateUtils.convertLocalDateFromServer(data.congresses[i].startDate);
                        data.congresses[i].endDate = DateUtils.convertLocalDateFromServer(data.congresses[i].endDate);
                    }
                    return data;
                }
            },
            'save': { method:'POST' },
            'update': { method:'PUT' },
            'delete':{ method:'DELETE'}
        });

        return service;
    }
})();
