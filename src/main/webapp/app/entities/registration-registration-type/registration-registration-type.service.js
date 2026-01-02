(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('RegistrationRegistrationType', RegistrationRegistrationType);

    RegistrationRegistrationType.$inject = ['$resource', 'DateUtils'];

    function RegistrationRegistrationType ($resource, DateUtils) {
        var resourceUrl =  'api/registration-registration-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByRegistrationId' : {
                method: 'GET',
                url: 'api/registrations/:id/registration-registration-types',
                isArray: true
            },
            'queryVMByRegistrationId' : {
                method: 'GET',
                url: 'api/registrations/:id/registration-registration-type-vms',
                isArray: true
            },
            'queryRegFeeVMByRegistrationTypeId' : {
                method: 'GET',
                url: 'api/registrations/:registrationId/registration-types/:registrationTypeId/calculate-reg-fee'
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createdDate = DateUtils.convertLocalDateFromServer(data.createdDate);
                        data.dateOfGroupPayment = DateUtils.convertLocalDateFromServer(data.dateOfGroupPayment);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.createdDate = DateUtils.convertLocalDateToServer(copy.createdDate);
                    copy.dateOfGroupPayment = DateUtils.convertLocalDateToServer(copy.dateOfGroupPayment);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.createdDate = DateUtils.convertLocalDateToServer(copy.createdDate);
                    copy.dateOfGroupPayment = DateUtils.convertLocalDateToServer(copy.dateOfGroupPayment);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
