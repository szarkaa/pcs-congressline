(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('OptionalProgramsMembers', OptionalProgramsMembers);

    OptionalProgramsMembers.$inject = ['$resource', 'DateUtils'];

    function OptionalProgramsMembers ($resource, DateUtils) {
        var resourceUrl =  'api/optional-program-members/:congressId';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
