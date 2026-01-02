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
            'queryVMByRegistrationId' : {
                method: 'GET',
                url: 'api/registrations/:id/ordered-optional-service-vms',
                isArray: true
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateOfGroupPayment = DateUtils.convertLocalDateFromServer(data.dateOfGroupPayment);
                        data.createdDate = DateUtils.convertLocalDateFromServer(data.createdDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dateOfGroupPayment = DateUtils.convertLocalDateToServer(copy.dateOfGroupPayment);
                    copy.createdDate = DateUtils.convertLocalDateToServer(copy.createdDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dateOfGroupPayment = DateUtils.convertLocalDateToServer(copy.dateOfGroupPayment);
                    copy.createdDate = DateUtils.convertLocalDateToServer(copy.createdDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
