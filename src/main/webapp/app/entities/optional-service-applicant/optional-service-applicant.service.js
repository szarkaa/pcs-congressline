(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('OptionalServiceApplicant', OptionalServiceApplicant);

    OptionalServiceApplicant.$inject = ['$resource', 'DateUtils'];

    function OptionalServiceApplicant ($resource, DateUtils) {
        var resourceUrl =  'api/optional-service-applicants/:optionalServiceId';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
