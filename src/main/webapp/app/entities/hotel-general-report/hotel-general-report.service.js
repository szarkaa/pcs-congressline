(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('HotelGeneralReport', HotelGeneralReport);

    HotelGeneralReport.$inject = ['$resource'];

    function HotelGeneralReport ($resource) {
        var resourceUrl =  'api/hotel-general-report/:meetingCode';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
