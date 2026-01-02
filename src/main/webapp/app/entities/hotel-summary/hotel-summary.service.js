(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('HotelSummary', HotelSummary);

    HotelSummary.$inject = ['$resource', 'DateUtils'];

    function HotelSummary ($resource, DateUtils) {
        var resourceUrl = 'api/hotel-summary/:meetingCode/:hotelId';

        return $resource(resourceUrl, {}, {
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        for (var i = 0; i < data.rows.length; i++) {
                            data.rows[i] = DateUtils.convertLocalDateFromServer(data.rows[i]);
                        }

                        for (var j = 0; j < data.cells.length; j++) {
                            data.cells[j].reservationDate = DateUtils.convertLocalDateFromServer(data.cells[j].reservationDate);
                        }
                    }
                    return data;
                }
            }
        });
    }
})();
