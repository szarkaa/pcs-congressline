(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('GroupDiscountPayment', GroupDiscountPayment);

    GroupDiscountPayment.$inject = ['$resource', 'DateUtils'];

    function GroupDiscountPayment ($resource, DateUtils) {
        var resourceUrl =  'api/group-discount-payments/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongress': {
                method: 'GET',
                url: 'api/group-discount-payments/congress/:id',
                isArray: true
            },
            'queryByPayingGroupId': {
                method: 'GET',
                url: 'api/group-discount-payments/paying-group/:id',
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
