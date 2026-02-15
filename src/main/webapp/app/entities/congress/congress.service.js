(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('Congress', Congress);

    Congress.$inject = ['$resource', 'DateUtils'];

    function Congress ($resource, DateUtils) {
        var resourceUrl =  'api/congresses/:id';

        return $resource(resourceUrl, {}, {
                'query': { method: 'GET', isArray: true},
                'queryStripped': { url: 'api/stripped-congresses',
                    method: 'GET',
                    isArray: true,
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        for (var i = 0; i < data.length; i++) {
                            data[i].startDate = DateUtils.convertLocalDateFromServer(data[i].startDate);
                            data[i].endDate = DateUtils.convertLocalDateFromServer(data[i].endDate);
                        }
                        return data;
                    }
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
            'getOnlineRegConfig': {
                method: 'GET',
                url: 'api/congresses/:id/online-reg-config'
            },
            'getOnlineRegCurrenciesByCongressId': {
                method: 'GET',
                url: 'api/congresses/:id/online-reg-currencies'
                ,isArray: true
            },
            'getOnlineRegCustomQuestions': {
                method: 'GET',
                url: 'api/congresses/:id/online/custom-questions',
                isArray: true
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
            'updateOnlineRegConfig': {
                method: 'PUT',
                url: 'api/congresses/online-reg-config',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
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
            },
            'migrateItems': {
                method: 'POST',
                url: 'api/congresses/migrate-items'
            }
        });
    }
})();
