(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('RegFeeDetailsByParticipant', RegFeeDetailsByParticipant);

    RegFeeDetailsByParticipant.$inject = ['$resource'];

    function RegFeeDetailsByParticipant ($resource) {
        var resourceUrl =  'api/regfee-details-by-participant';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
