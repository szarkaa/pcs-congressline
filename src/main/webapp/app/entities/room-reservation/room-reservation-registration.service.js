(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('RoomReservationRegistration', RoomReservationRegistration);

    RoomReservationRegistration.$inject = ['$resource', 'DateUtils'];

    function RoomReservationRegistration ($resource, DateUtils) {
        var resourceUrl =  'api/room-reservation-registrations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateOfGroupPayment = DateUtils.convertLocalDateFromServer(data.dateOfGroupPayment);
                        data.createdDate = DateUtils.convertLocalDateFromServer(data.createdDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dateOfGroupPayment = DateUtils.convertLocalDateToServer(copy.dateOfGroupPayment);
                    copy.createdDate = DateUtils.convertLocalDateToServer(copy.createdDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dateOfGroupPayment = DateUtils.convertLocalDateToServer(copy.dateOfGroupPayment);
                    copy.createdDate = DateUtils.convertLocalDateToServer(copy.createdDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
