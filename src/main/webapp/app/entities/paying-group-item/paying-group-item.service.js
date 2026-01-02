(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('PayingGroupItem', PayingGroupItem);

    PayingGroupItem.$inject = ['$resource', 'DateUtils'];

    function PayingGroupItem ($resource, DateUtils) {
        var resourceUrl =  'api/paying-group-items/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByPayingGroup': {
                method: 'GET',
                url: 'api/paying-group/:id/paying-group-items',
                isArray: true
            },
            'queryByCongressAndItemType': {
                method: 'GET',
                url: 'api/congress/:id/paying-group/paying-group-items/:itemType',
                isArray: true
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.hotelDateFrom = DateUtils.convertLocalDateFromServer(data.hotelDateFrom);
                        data.hotelDateTo = DateUtils.convertLocalDateFromServer(data.hotelDateTo);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.hotelDateFrom = DateUtils.convertLocalDateToServer(copy.hotelDateFrom);
                    copy.hotelDateTo = DateUtils.convertLocalDateToServer(copy.hotelDateTo);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.hotelDateFrom = DateUtils.convertLocalDateToServer(copy.hotelDateFrom);
                    copy.hotelDateTo = DateUtils.convertLocalDateToServer(copy.hotelDateTo);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
