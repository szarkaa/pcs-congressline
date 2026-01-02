(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('Email', Email);

    Email.$inject = ['$resource', 'DateUtils'];

    function Email ($resource, DateUtils) {
        var resourceUrl =  'api/email/:id';

        return $resource(resourceUrl, {}, {
            'send': { method:'POST', url: 'api/email/send' }
        });
    }
})();
