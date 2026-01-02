(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('GroupDiscountInvoice', GroupDiscountInvoice);

    GroupDiscountInvoice.$inject = ['$resource', 'DateUtils'];

    function GroupDiscountInvoice ($resource, DateUtils) {
        var resourceUrl =  'api/group-discount-invoices/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongress': {
                method: 'GET',
                url: 'api/group-discount-invoices/congress/:id',
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
                        data.dateOfGrougPayment = DateUtils.convertLocalDateFromServer(data.dateOfGrougPayment);
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
                    copy.dateOfGrougPayment = DateUtils.convertLocalDateToServer(copy.dateOfGrougPayment);
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
                    copy.dateOfGrougPayment = DateUtils.convertLocalDateToServer(copy.dateOfGrougPayment);
                    return angular.toJson(copy);
                }
            },
            'saveAndSendEmail': {
                method: 'POST',
                url: 'api/group-discount-invoices/save-and-send-email',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate);
                    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate);
                    copy.dateOfFulfilment = DateUtils.convertLocalDateToServer(copy.dateOfFulfilment);
                    copy.paymentDeadline = DateUtils.convertLocalDateToServer(copy.paymentDeadline);
                    copy.createdDate = DateUtils.convertLocalDateToServer(copy.createdDate);
                    copy.dateOfGrougPayment = DateUtils.convertLocalDateToServer(copy.dateOfGrougPayment);
                    return angular.toJson(copy);
                }
            },
            'savePaymentDate': {
                method: 'PUT',
                url: 'api/group-discount-invoices/set-payment-date',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.paymentDate = DateUtils.convertLocalDateToServer(copy.paymentDate);
                    return angular.toJson(copy);
                }
            },
            'storno': {
                method: 'GET',
                url: 'api/group-discount-invoices/:id/storno'
            }
        });
    }
})();
