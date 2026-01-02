(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('ChargedService', ChargedService);

    ChargedService.$inject = ['$resource', 'DateUtils'];

    function ChargedService ($resource, DateUtils) {
        var resourceUrl =  'api/charged-services/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByRegistrationId': {
                url: '/api/registrations/:id/charged-services',
                method: 'GET',
                isArray: true
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateOfPayment = DateUtils.convertLocalDateFromServer(data.dateOfPayment);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dateOfPayment = DateUtils.convertLocalDateToServer(copy.dateOfPayment);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dateOfPayment = DateUtils.convertLocalDateToServer(copy.dateOfPayment);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
