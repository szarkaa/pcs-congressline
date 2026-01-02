(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('RegFeeDetails', RegFeeDetails);

    RegFeeDetails.$inject = ['$resource'];

    function RegFeeDetails ($resource) {
        var resourceUrl =  'api/regfee-details/:congressId';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
