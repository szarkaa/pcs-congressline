(function () {
    'use strict';

    angular.module('pcsApp')
        .factory('OnlineReg', OnlineReg);

    OnlineReg.$inject = ['$resource', 'DateUtils'];

    function OnlineReg($resource, DateUtils) {
        var resourceUrl = '/api/registration/online';

        return $resource(resourceUrl, {}, {
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.arrivalDate = DateUtils.convertLocalDateToServer(copy.arrivalDate);
                    copy.departureDate = DateUtils.convertLocalDateToServer(copy.departureDate);
                    return angular.toJson(copy);
                }
            },
            'setStripePaymentStatus': {
                method: 'POST',
                url: resourceUrl + '/stripe/payment/status'
            },
            'getCongress': {
                method: 'GET',
                url: resourceUrl + '/congress/:uuid',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.startDate = DateUtils.convertLocalDateFromServer(data.startDate);
                        data.endDate = DateUtils.convertLocalDateFromServer(data.endDate);
                    }
                    return data;
                }
            },
            'getDiscountCode': {
                method: 'GET',
                url: resourceUrl + '/congress/:uuid/online-reg-discount-code/:code'
            },
            'getPaymentResult': {
                method: 'GET',
                url: resourceUrl + '/payment/result/:txid'
            },
            'queryCountries': { method: 'GET', url: resourceUrl + '/country', isArray: true},
            'queryRegistrationTypes': { method: 'GET', url: resourceUrl + '/congress/:uuid/registration-type/:currency', isArray: true},
            'queryHotelRooms': { method: 'GET', url: resourceUrl + '/congress/:uuid/hotel-room/:currency', isArray: true},
            'queryOptionalServices': { method: 'GET', url: resourceUrl + '/congress/:uuid/optional-service/:currency', isArray: true},
            'queryCustomQuestions': { method: 'GET', url: resourceUrl + '/congress/:uuid/custom-questions/:currency', isArray: true}
        });
    }
})();