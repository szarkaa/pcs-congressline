(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('VatInfo', VatInfo);

    VatInfo.$inject = ['$resource'];

    function VatInfo ($resource) {
        var resourceUrl =  'api/vat-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongress': {
                method: 'GET',
                url: 'api/vat-infos/congress/:id'
                ,isArray: true},
            'queryForCongress': {
                method: 'GET',
                url: 'api/vat-infos/all/congress/:id'
                ,isArray: true},
            'queryForCongressAndItemType': {
                method: 'GET',
                url: 'api/vat-infos/all/congress/:id/item-type/:itemType'
                ,isArray: true},
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
