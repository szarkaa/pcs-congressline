(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('MiscInvoice', MiscInvoice);

    MiscInvoice.$inject = ['$resource', 'DateUtils'];

    function MiscInvoice ($resource, DateUtils) {
        var resourceUrl =  'api/misc-invoices/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'queryByCongress': {
                method: 'GET',
                url: 'api/misc-invoices/congress/:id',
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
                    copy.startDate = DateUtils.convertLocalDateToServer(copy.startDate);
                    copy.endDate = DateUtils.convertLocalDateToServer(copy.endDate);
                    copy.dateOfFulfilment = DateUtils.convertLocalDateToServer(copy.dateOfFulfilment);
                    copy.paymentDeadline = DateUtils.convertLocalDateToServer(copy.paymentDeadline);
                    copy.dateOfGroupPayment = DateUtils.convertLocalDateToServer(copy.dateOfGroupPayment);
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
                    copy.dateOfGroupPayment = DateUtils.convertLocalDateToServer(copy.dateOfGroupPayment);
                    copy.createdDate = DateUtils.convertLocalDateToServer(copy.createdDate);
                    return angular.toJson(copy);
                }
            },
            'saveAndSendEmail': {
                method: 'POST',
                url: 'api/misc-invoices/save-and-send-email',
                transformRequest: function (data) {
                    data.startDate = DateUtils.convertLocalDateToServer(data.startDate);
                    data.endDate = DateUtils.convertLocalDateToServer(data.endDate);
                    data.dateOfFulfilment = DateUtils.convertLocalDateToServer(data.dateOfFulfilment);
                    data.paymentDeadline = DateUtils.convertLocalDateToServer(data.paymentDeadline);
                    data.dateOfGroupPayment = DateUtils.convertLocalDateToServer(data.dateOfGroupPayment);
                    data.createdDate = DateUtils.convertLocalDateToServer(data.createdDate);
                    return angular.toJson(data);
                }
            },
            'savePaymentDate': {
                method: 'PUT',
                url: 'api/misc-invoices/set-payment-date',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.paymentDate = DateUtils.convertLocalDateToServer(copy.paymentDate);
                    return angular.toJson(copy);
                }
            },
            'storno': {
                method: 'GET',
                url: 'api/misc-invoices/:id/storno'
            }
        });
    }
})();
