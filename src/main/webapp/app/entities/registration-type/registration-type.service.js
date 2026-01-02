(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('RegistrationType', RegistrationType);

    RegistrationType.$inject = ['$resource', 'DateUtils'];

    function RegistrationType ($resource, DateUtils) {
        var resourceUrl =  'api/registration-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongress': {
                method: 'GET',
                url: 'api/registration-types/congress/:id',
                isArray: true
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.firstDeadline = DateUtils.convertLocalDateFromServer(data.firstDeadline);
                        data.secondDeadline = DateUtils.convertLocalDateFromServer(data.secondDeadline);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.firstDeadline = DateUtils.convertLocalDateToServer(copy.firstDeadline);
                    copy.secondDeadline = DateUtils.convertLocalDateToServer(copy.secondDeadline);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.firstDeadline = DateUtils.convertLocalDateToServer(copy.firstDeadline);
                    copy.secondDeadline = DateUtils.convertLocalDateToServer(copy.secondDeadline);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
