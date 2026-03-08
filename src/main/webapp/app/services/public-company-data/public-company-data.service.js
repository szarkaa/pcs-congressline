(function () {
    'use strict';

    angular
        .module('pcsApp')
        .factory('PublicCompanyData', PublicCompanyData);

    PublicCompanyData.$inject = ['$resource', 'DateUtils'];

    function PublicCompanyData ($resource, DateUtils) {
        return $resource('api/public-company-data', {}, {
            'search': {url: 'api/public-company-data/search', method: 'GET', isArray: true},
            'detail': {url: 'api/public-company-data/detail', method: 'GET'}
        });
    }
})();
