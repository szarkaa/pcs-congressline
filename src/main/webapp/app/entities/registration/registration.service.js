(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('Registration', Registration);

    Registration.$inject = ['$resource', 'DateUtils'];

    function Registration ($resource, DateUtils) {
        var resourceUrl =  'api/registrations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateOfApp = DateUtils.convertLocalDateFromServer(data.dateOfApp);
                    }
                    return data;
                }
            },
            'getDefault': {
                method: 'GET',
                url: 'api/registrations/default/congress/:id',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateOfApp = DateUtils.convertLocalDateFromServer(data.dateOfApp);
                        return data;
                    }
                    else {
                        return null;
                    }
                }
            },
            'getNextIdAfterDeletedId': {
                method: 'GET',
                url: 'api/registrations/nextid-after-the-deleted-id/:id/congress/:congressId',
                transformResponse: function (data) {
                    if (data) {
                        return angular.fromJson(data);
                    }
                    else {
                        return null;
                    }
                }
            },
            'getSummary': {
                method: 'GET',
                url: 'api/registrations/summary/congress/:id',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        return data;
                    }
                    else {
                        return null;
                    }
                }
            },
            'queryByCongress': {
                method: 'GET',
                url: 'api/registrations/congress/:id',
                isArray: true
            },
            'queryVMByCongress': {
                method: 'GET',
                url: 'api/registrations/vm/congress/:id',
                isArray: true
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dateOfApp = DateUtils.convertLocalDateToServer(copy.dateOfApp);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dateOfApp = DateUtils.convertLocalDateToServer(copy.dateOfApp);
                    return angular.toJson(copy);
                }
            },
            'upload': {
                method: 'POST', url: '/api/registrations/upload', isArray: true
            }
        });
    }
})();
