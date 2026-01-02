(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('AccPeople', AccPeople);

    AccPeople.$inject = ['$resource'];

    function AccPeople ($resource) {
        var resourceUrl =  'api/acc-people/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByRegistrationRegistrationType': {
                method: 'GET',
                url: 'api/registration-registration-type/:id/acc-peoples',
                isArray: true},
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
