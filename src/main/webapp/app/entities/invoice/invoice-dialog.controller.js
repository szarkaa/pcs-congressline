(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('InvoiceDialogController', InvoiceDialogController);

    InvoiceDialogController.$inject = ['$timeout', 'registration', 'registrationRegistrationTypes', 'roomReservations', 'orderedOptionalServices',
        'chargedServices', 'invoice', 'invoicedChargeableItemIds', 'invoicedChargedServiceIds'];

    function InvoiceDialogController ($timeout, registration, registrationRegistrationTypes, roomReservations, orderedOptionalServices,
                                      chargedServices, invoice, invoicedChargeableItemIds, invoicedChargedServiceIds) {
        var vm = this;
        vm.invoice = invoice;
        vm.invoice.lastName = registration.lastName;
        vm.invoice.firstName = registration.firstName;
        vm.invoice.invoiceName = registration.invoiceName;
        vm.invoice.name1 = registration.invoiceName ? registration.invoiceName : registration.lastName + ' ' + registration.firstName;
        vm.invoice.vatRegNumber = registration.invoiceTaxNumber ? registration.invoiceTaxNumber : (registration.workplace ? (registration.workplace.vatRegNumber || '') : '');
        vm.invoice.country = registration.invoiceCountry ? registration.invoiceCountry.code : (registration.country ? registration.country.code : null);
        vm.invoice.city = registration.invoiceCity ? registration.invoiceCity : registration.city;
        vm.invoice.zipCode = registration.invoiceZipCode ? registration.invoiceZipCode : registration.zipCode;
        vm.invoice.street = registration.invoiceAddress ? registration.invoiceAddress : registration.street;
        vm.invoice.registration = registration;

        vm.registrationRegistrationTypes = registrationRegistrationTypes;
        vm.roomReservations = roomReservations;
        vm.orderedOptionalServices = orderedOptionalServices;
        vm.chargedServices = chargedServices;
        vm.invoicedChargeableItemIds = invoicedChargeableItemIds;
        vm.invoicedChargedServiceIds = invoicedChargedServiceIds;
        vm.unInvoiceableChargeableItems = [];
        vm.unInvoiceableChargedServiceIds = [];

        vm.calculateTotalDue = calculateTotalDue;
        vm.isInvoiceable = isInvoiceable;
        vm.getPriceAlreadyPaid = getPriceAlreadyPaid;
        vm.getGroupPaid = getGroupPaid;
        vm.getPriceToBePaid = getPriceToBePaid;
        vm.sumPriceToBePaid = sumPriceToBePaid;
        vm.sumGroupAlreadyPaid = sumGroupAlreadyPaid;
        vm.sumPriceAlreadyPaid = sumPriceAlreadyPaid;
        vm.isChargeableItemDisabled = isChargeableItemDisabled;
        vm.isChargedServiceDisabled = isChargedServiceDisabled;

        initIgnoredChargedServices();
        initIgnoredChargeableItems();

        function initIgnoredChargeableItems() {
            // init by already invoiced items
            if (vm.invoice.ignoredChargeableItems === null) {
                vm.invoice.ignoredChargeableItems = {};
                for (var i = 0; i < vm.invoicedChargeableItemIds.length; i++) {
                    vm.invoice.ignoredChargeableItems[vm.invoicedChargeableItemIds[i].toString()] = true;
                }
            }

            //init by the items where price have to be paid is zero, so it can not be on any invoice
            for (var i = 0; i < vm.registrationRegistrationTypes.length; i++) {
                var rrt = vm.registrationRegistrationTypes[i];
                if (rrt.chargeableItemPrice === vm.getGroupPaid(rrt)) {
                    vm.invoice.ignoredChargeableItems[rrt.id.toString()] = true;
                    vm.unInvoiceableChargeableItems.push(rrt.id);
                }
            }

            for (var i = 0; i < vm.roomReservations.length; i++) {
                var rr = vm.roomReservations[i];
                if (rr.chargeableItemPrice === vm.getGroupPaid(rr)) {
                    vm.invoice.ignoredChargeableItems[rr.id.toString()] = true;
                    vm.unInvoiceableChargeableItems.push(rr.id);
                }
            }

            for (i = 0; i < vm.orderedOptionalServices.length; i++) {
                var oos = vm.orderedOptionalServices[i];
                if (oos.chargeableItemPrice === vm.getGroupPaid(oos)) {
                    vm.invoice.ignoredChargeableItems[oos.id.toString()] = true;
                    vm.unInvoiceableChargeableItems.push(oos.id);
                }
            }

        }

        function initIgnoredChargedServices() {
            if (vm.invoice.ignoredChargedServices === null) {
                vm.invoice.ignoredChargedServices = {};
                for (var i = 0; i < vm.chargedServices.length; i++) {
                    var cs = vm.chargedServices[i];
                    if (cs.amount < 0) {
                        vm.unInvoiceableChargedServiceIds.push(cs.id);
                        vm.invoice.ignoredChargedServices[cs.id] = true;
                    }
                }

                for (i = 0; i < vm.invoicedChargedServiceIds.length; i++) {
                    vm.invoice.ignoredChargedServices[vm.invoicedChargedServiceIds[i].toString()] = true;
                }
            }
        }

        vm.toggleIgnoreChargeableItem = function (chargeableItemId) {

        };

        vm.toggleIgnoreChargedService = function (chargedServiceId) {

        };

        function isChargeableItemDisabled(id) {
            var disabled = vm.invoicedChargeableItemIds.indexOf(id) > -1;
            if (!disabled) {
                for (var i = 0; i < vm.unInvoiceableChargeableItems.length; i++) {
                    if (vm.unInvoiceableChargeableItems[i] === id) {
                        disabled = true;
                        break;
                    }
                }
            }
            return disabled;
        }

        function isChargedServiceDisabled(id) {
            var disabled = vm.invoicedChargedServiceIds.indexOf(id) > -1;
            if (!disabled) {
                for (var i = 0; i < vm.unInvoiceableChargedServiceIds.length; i++) {
                    if (vm.unInvoiceableChargedServiceIds[i] === id) {
                        disabled = true;
                        break;
                    }
                }
            }
            return disabled;
        }

        function isInvoiceable() {
            var itemNumInAllLists = vm.registrationRegistrationTypes.length +
                vm.roomReservations.length + vm.orderedOptionalServices.length + vm.chargedServices.length;
            var notAllIgnored = getNumberOfIgnoredItems() < itemNumInAllLists;
            return notAllIgnored && !(vm.invoicedChargeableItemIds.length == vm.registrationRegistrationTypes.length +
                vm.roomReservations.length + vm.orderedOptionalServices.length &&
                vm.invoicedChargedServiceIds.length == vm.chargedServices.length);
        }

        function getNumberOfIgnoredItems() {
            var result = 0;
            for (var prop in vm.invoice.ignoredChargeableItems) {
                if (vm.invoice.ignoredChargeableItems.hasOwnProperty(prop) && vm.invoice.ignoredChargeableItems[prop]) {
                    result++;
                }
            }

            for (var prop in vm.invoice.ignoredChargedServices) {
                if (vm.invoice.ignoredChargedServices.hasOwnProperty(prop) && vm.invoice.ignoredChargedServices[prop]) {
                    result++;
                }
            }
            return result;
        }

        function getPriceAlreadyPaid(chargeableItemId) {
            var i, amount = 0;
            for (i = 0; i < vm.chargedServices.length; i++) {
                if (vm.chargedServices[i].chargeableItemId === chargeableItemId) {
                    amount += vm.chargedServices[i].amount;
                }
            }
            return amount;
        }

        function getGroupPaid(chargeableItem) {
            return chargeableItem.chargeableItemPrice - chargeableItem.priceWithDiscount;
        }

        function getPriceToBePaid(price, chargeableItemId) {
            return price - vm.getPriceAlreadyPaid(chargeableItemId)
        }

        function sumPriceAlreadyPaid(list) {
            var i, sumAmount = 0;
            for (i = 0; i < list.length; i++) {
                sumAmount  += vm.getPriceAlreadyPaid(list[i].id);
            }
            return sumAmount;
        }

        function sumGroupAlreadyPaid(list) {
            var i, sumAmount = 0;
            for (i = 0; i < list.length; i++) {
                sumAmount  += vm.getGroupPaid(list[i]);
            }
            return sumAmount;
        }

        function sumPriceToBePaid(list) {
            var i, sumAmount = 0;
            for (i = 0; i < list.length; i++) {
                sumAmount  += vm.getPriceToBePaid(list[i].priceWithDiscount, list[i].id);
            }
            return sumAmount;
        }

        function calculateTotalDue() {
            var i, total = 0;
            for (i = 0; i < vm.registrationRegistrationTypes.length; i++) {
                if (!vm.invoice.ignoredChargeableItems[vm.registrationRegistrationTypes[i].id]) {
                    total += vm.registrationRegistrationTypes[i].priceWithDiscount;
                }
            }

            for (i = 0; i < vm.roomReservations.length; i++) {
                if (!vm.invoice.ignoredChargeableItems[vm.roomReservations[i].id]) {
                    total += vm.roomReservations[i].priceWithDiscount;
                }
            }

            for (i = 0; i < vm.orderedOptionalServices.length; i++) {
                if (!vm.invoice.ignoredChargeableItems[vm.orderedOptionalServices[i].id]) {
                    total += vm.orderedOptionalServices[i].priceWithDiscount;
                }
            }

            for (i = 0; i < vm.chargedServices.length; i++) {
                if (!vm.invoice.ignoredChargedServices[vm.chargedServices[i].id]) {
                    total -= vm.chargedServices[i].amount;
                }
            }

            return total;
        }

        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;

/*
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });
*/

        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;
        vm.datePickerOpenStatus.paymentDeadline = false;
        vm.datePickerOpenStatus.createdDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
