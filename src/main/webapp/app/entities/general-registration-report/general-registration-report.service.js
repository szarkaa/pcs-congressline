(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('GeneralRegistrationReport', GeneralRegistrationReport);

    GeneralRegistrationReport.$inject = ['$resource'];

    function GeneralRegistrationReport ($resource) {
        var resourceUrl =  'api/general-registration-report';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'sendGeneralEmailToAll': {
                method: 'POST',
                url: 'api/general-registration-report/send-general-email-to-all'
            }
        });
    }
})();
