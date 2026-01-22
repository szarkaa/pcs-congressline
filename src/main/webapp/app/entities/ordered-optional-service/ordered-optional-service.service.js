(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('OrderedOptionalService', OrderedOptionalService);

    OrderedOptionalService.$inject = ['$resource', 'DateUtils'];

    function OrderedOptionalService ($resource, DateUtils) {
        var resourceUrl =  'api/ordered-optional-services/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByRegistrationId' : {
                method: 'GET',
                url: 'api/registrations/:id/ordered-optional-services',
                isArray: true
            },
            'queryDTOByRegistrationId' : {
                method: 'GET',
                url: 'api/registrations/:id/ordered-optional-service-dtos',
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
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
