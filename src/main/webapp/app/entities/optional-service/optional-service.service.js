(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('OptionalService', OptionalService);

    OptionalService.$inject = ['$resource', 'DateUtils'];

    function OptionalService ($resource, DateUtils) {
        var resourceUrl =  'api/optional-services/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongress': {
                method: 'GET',
                url: 'api/optional-services/congress/:id',
                isArray: true
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.startDate = DateUtils.convertLocalDateFromServer(data.startDate);
                        data.endDate = DateUtils.convertLocalDateFromServer(data.endDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate);
                    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate);
                    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
