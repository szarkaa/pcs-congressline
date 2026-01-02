(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('BackendOnlineReg', BackendOnlineReg);

    BackendOnlineReg.$inject = ['$resource', 'DateUtils'];

    function BackendOnlineReg ($resource, DateUtils) {
        var resourceUrl =  'api/backend-online-regs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongress': {
                method: 'GET',
                url: 'api/backend-online-regs/congress/:id',
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
            'queryCustomAnswers': {
                method: 'GET',
                url: resourceUrl + '/custom-answers',
                isArray: true
            },
            'accept': {
                method: 'POST'
            },
            'acceptAll': {
                method: 'POST',
                url: 'api/backend-online-regs/confirmation/all'
            },
            'deleteAll': {
                method: 'POST',
                url: 'api/backend-online-regs/delete/all'
            }
        });
    }
})();
