(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('GroupDiscountItem', GroupDiscountItem);

    GroupDiscountItem.$inject = ['$resource', 'DateUtils'];

    function GroupDiscountItem ($resource, DateUtils) {
        var resourceUrl =  'api/group-discount-items/:meetingCode/:payingGroupId/:chargeableItemType';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
/*
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
*/
        });
    }
})();
