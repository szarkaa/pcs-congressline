(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('Invoice', Invoice);

    Invoice.$inject = ['$resource', 'DateUtils'];

    function Invoice ($resource, DateUtils) {
        var resourceUrl =  'api/invoices/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByRegistrationId': {
                method: 'GET',
                url: 'api/registrations/:id/invoices',
                isArray: true
            },
            'getInvoicedChargeableItemIdsByRegistrationId': {
                method: 'GET',
                url: 'api/invoices/:id/invoiced-chargeable-items',
                isArray: true
            },
            'getInvoicedChargedServiceIdsByRegistrationId': {
                method: 'GET',
                url: 'api/invoices/:id/invoiced-charged-services',
                isArray: true
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.startDate = DateUtils.convertLocalDateFromServer(data.startDate);
                        data.endDate = DateUtils.convertLocalDateFromServer(data.endDate);
                        data.dateOfFulfilment = DateUtils.convertLocalDateFromServer(data.dateOfFulfilment);
                        data.paymentDeadline = DateUtils.convertLocalDateFromServer(data.paymentDeadline);
                        data.createdDate = DateUtils.convertLocalDateFromServer(data.createdDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate);
                    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate);
                    copy.dateOfFulfilment = DateUtils.convertLocalDateToServer(copy.dateOfFulfilment);
                    copy.paymentDeadline = DateUtils.convertLocalDateToServer(copy.paymentDeadline);
                    copy.createdDate = DateUtils.convertLocalDateToServer(copy.createdDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate);
                    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate);
                    copy.dateOfFulfilment = DateUtils.convertLocalDateToServer(copy.dateOfFulfilment);
                    copy.paymentDeadline = DateUtils.convertLocalDateToServer(copy.paymentDeadline);
                    copy.createdDate = DateUtils.convertLocalDateToServer(copy.createdDate);
                    return angular.toJson(copy);
                }
            },
            'saveAndSendEmail': {
                method: 'POST',
                url: 'api/invoices/save-and-send-email',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate);
                    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate);
                    copy.dateOfFulfilment = DateUtils.convertLocalDateToServer(copy.dateOfFulfilment);
                    copy.paymentDeadline = DateUtils.convertLocalDateToServer(copy.paymentDeadline);
                    copy.createdDate = DateUtils.convertLocalDateToServer(copy.createdDate);
                    return angular.toJson(copy);
                }
            },
            'savePaymentDate': {
                method: 'PUT',
                url: 'api/invoices/set-payment-date',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.paymentDate = DateUtils.convertLocalDateToServer(copy.paymentDate);
                    return angular.toJson(copy);
                }
            },
            'sendEmail': {
                method: 'POST',
                url: 'api/invoices/resend-email',
                transformRequest: function (data) {
                    return angular.toJson(data);
                }
            },
            'storno': {
                method: 'GET',
                url: 'api/invoices/:id/storno'
            }
        });
    }
})();
