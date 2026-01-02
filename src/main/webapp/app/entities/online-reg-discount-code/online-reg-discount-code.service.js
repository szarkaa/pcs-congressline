(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('OnlineRegDiscountCode', OnlineRegDiscountCode);

    OnlineRegDiscountCode.$inject = ['$resource'];

    function OnlineRegDiscountCode ($resource) {
        var resourceUrl = 'api/online-reg-discount-codes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongressId': {
                method: 'GET',
                url: resourceUrl +'/congress',
                isArray: true
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
