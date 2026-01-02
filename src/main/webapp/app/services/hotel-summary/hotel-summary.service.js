(function() {
    'use strict';

    angular.module('pcsApp')
        .factory('HotelSummaryFilter', HotelSummaryFilter);

    HotelSummaryFilter.$inject = ['$cookies', '$state'];

    function HotelSummaryFilter ($cookies, $state) {
        var hotelSummaryFilter = null;

        return {
            setHotelSummaryFilter: function (filter) {
                if (filter) {
                    hotelSummaryFilter = {
                        congressHotel: filter.congressHotel
                    };
                    $cookies.putObject('hotel-summary-report-filter', hotelSummaryFilter);
                }
                else {
                    hotelSummaryFilter = null;
                    $cookies.remove('hotel-summary-report-filter');
                }

            },
            getHotelSummaryFilter: function () {
                if (hotelSummaryFilter == null) {
                    hotelSummaryFilter = $cookies.getObject('hotel-summary-report-filter');
                    if (!hotelSummaryFilter) {
                        return {
                            congressHotel: null
                        };
                    }
                }
                return hotelSummaryFilter;
            }
        };
    }
})();
