(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('Rate', Rate);

    Rate.$inject = ['$resource', 'DateUtils'];

    function Rate ($resource, DateUtils) {
        var resourceUrl =  'api/rates/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.valid = DateUtils.convertLocalDateFromServer(data.valid);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.valid = DateUtils.convertLocalDateToServer(copy.valid);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.valid = DateUtils.convertLocalDateToServer(copy.valid);
                    return angular.toJson(copy);
                }
            },
            'getCurrentRate': {
                method: 'GET',
                url: 'api/rates/current/:currency',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.valid = DateUtils.convertLocalDateFromServer(data.valid);
                    }
                    return data;
                }
            },
            'getRatesForDate': {
                method: 'GET',
                url: 'api/rates/:valid-date/:currency',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.valid = DateUtils.convertLocalDateFromServer(data.valid);
                    }
                    return data;
                }
            }
        });
    }
})();
