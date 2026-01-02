(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('ConfirmationController', ConfirmationController);

    ConfirmationController.$inject = ['$state', 'registration', 'registrationRegistrationTypes', 'roomReservations',
        'orderedOptionalServices', 'chargedServices', 'OptionalText', 'CongressSelector', 'Confirmation'];

    function ConfirmationController ($state, registration, registrationRegistrationTypes, roomReservations,
                                     orderedOptionalServices, chargedServices, OptionalText, CongressSelector, Confirmation) {
        var vm = this;
        vm.isShowConfirmationPanel = false;
        vm.confirmationTitleType = 'CONFIRMATION';
        vm.language = 'hu';
        vm.optionalText = '';
        vm.ignoredChargeableItems = {};
        vm.ignoredChargedServices = {};
        vm.registration = registration;
        vm.registrationRegistrationTypes = registrationRegistrationTypes;
        vm.roomReservations = roomReservations;
        vm.orderedOptionalServices = orderedOptionalServices;
        vm.chargedServices = chargedServices;
        OptionalText.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.optionalTexts = result;
        });

        vm.showConfirmationPanel = showConfirmationPanel;
        vm.printConfirmation = printConfirmation;
        vm.sendAndPrintConfirmation = sendAndPrintConfirmation;

        function showConfirmationPanel() {
            vm.confirmationEmail = vm.registration.email;
            vm.isShowConfirmationPanel = true;
        }

        function printConfirmation() {
            vm.isSaving = true;
            Confirmation.printConfirmation(createConfirmationForPrinting());
            //vm.isSaving = false;
            $state.go('registration', {id: registration.id});
        }

        function sendAndPrintConfirmation() {
            vm.isSaving = true;
            Confirmation.sendAndPrintConfirmation(createConfirmationForPrinting());
            vm.isSaving = false;
            $state.go('registration', {id: registration.id});
        }

        function createConfirmationForPrinting() {
            var conf = {};
            conf.confirmationTitleType = vm.confirmationTitleType;
            conf.language = vm.language;
            conf.optionalText = vm.optionalText;
            conf.registrationId = vm.registration.id;
            conf.customConfirmationEmail = vm.confirmationEmail;
            conf.congressId = CongressSelector.getSelectedCongress().id;

            // alter ignored items from obj properties to list of numbers
            conf.ignoredChargeableItemIdList = [];
            for (var prop in vm.ignoredChargeableItems) {
                if (vm.ignoredChargeableItems.hasOwnProperty(prop) && vm.ignoredChargeableItems[prop]) {
                    conf.ignoredChargeableItemIdList.push(parseInt(prop, 10));
                }
            }

            conf.ignoredChargedServiceIdList = [];
            for (var prop in vm.ignoredChargedServices) {
                if (vm.ignoredChargedServices.hasOwnProperty(prop) && vm.ignoredChargedServices[prop]) {
                    conf.ignoredChargedServiceIdList.push(parseInt(prop, 10));
                }
            }


            return conf;
        }

        function setOptionalTextMessage(text) {
            vm.optionalText = text;
        }

        vm.toggleIgnoreChargeableItem = toggleIgnoreChargeableItem;
        vm.toggleIgnoreChargedService = toggleIgnoreChargedService;
        vm.setOptionalTextMessage = setOptionalTextMessage;
        vm.getPriceAlreadyPaid = getPriceAlreadyPaid;
        vm.getGroupPaid = getGroupPaid;
        vm.getPriceToBePaid = getPriceToBePaid;
        vm.sumPriceToBePaid = sumPriceToBePaid;
        vm.sumGroupAlreadyPaid = sumGroupAlreadyPaid;
        vm.sumPriceAlreadyPaid = sumPriceAlreadyPaid;
        vm.calculateTotalDue = calculateTotalDue;

        function toggleIgnoreChargeableItem(chargeableItemId) {
            //vm.ignoredChargeableItems[chargeableItemId.toString()] = !vm.ignoredChargeableItems[chargeableItemId.toString()];
        }

        function toggleIgnoreChargedService (chargedServiceId) {

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

        function calculateTotalDue() {
            var i, total = 0;
            for (i = 0; i < vm.registrationRegistrationTypes.length; i++) {
                if (!vm.ignoredChargeableItems[vm.registrationRegistrationTypes[i].id]) {
                    total += vm.registrationRegistrationTypes[i].priceWithDiscount;
                }
            }

            for (i = 0; i < vm.roomReservations.length; i++) {
                if (!vm.ignoredChargeableItems[vm.roomReservations[i].id]) {
                    total += vm.roomReservations[i].priceWithDiscount;
                }
            }

            for (i = 0; i < vm.orderedOptionalServices.length; i++) {
                if (!vm.ignoredChargeableItems[vm.orderedOptionalServices[i].id]) {
                    total += vm.orderedOptionalServices[i].priceWithDiscount;
                }
            }

            for (i = 0; i < vm.chargedServices.length; i++) {
                if (!vm.ignoredChargedServices[vm.chargedServices[i].id]) {
                    total -= vm.chargedServices[i].amount;
                }
            }

            return total;
        }

    }
})();