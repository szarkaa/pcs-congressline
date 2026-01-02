(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('InvoiceController', InvoiceController);

    InvoiceController.$inject = ['$scope', '$state', 'invoices', 'registration', 'registrationRegistrationTypes',
        'roomReservations', 'orderedOptionalServices', 'Rate'];

    function InvoiceController ($scope, $state, invoices, registration, registrationRegistrationTypes,
        roomReservations, orderedOptionalServices, Rate) {
        var vm = this;

        vm.registration = registration;
        vm.invoices = invoices;
        vm.registrationRegistrationTypes = registrationRegistrationTypes;
        vm.roomReservations = roomReservations;
        vm.orderedOptionalServices = orderedOptionalServices;

        vm.hasValidCurrencyRate = true;

        checkValidCurrencyRate();


        vm.downloadPdf = function (invoiceId) {
            //var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
            window.open('/api/invoices/' + invoiceId + '/pdf', '_blank');
        };

        function checkValidCurrencyRate() {
            var currency;
            if (vm.registrationRegistrationTypes.length) {
                currency = vm.registrationRegistrationTypes[0].chargeableItemCurrency;
            }
            else if (vm.roomReservations.length) {
                currency = vm.roomReservations[0].chargeableItemCurrency;
            }
            else if (vm.orderedOptionalServices.length) {
                currency = vm.orderedOptionalServices[0].chargeableItemCurrency;
            }

            if (currency && currency !== 'HUF') {
                Rate.getCurrentRate({currency: currency}, function (result) {
                    vm.hasValidCurrencyRate = true;
                }, function (result) { vm.hasValidCurrencyRate = false; });
            }
            else {
                vm.hasValidCurrencyRate = !currency || currency === 'HUF' || false;
            }
        }


    }
})();
