(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('InvoiceSetupDialogController', InvoiceSetupDialogController);

    InvoiceSetupDialogController.$inject = ['$timeout', '$scope', '$state', '$stateParams', 'registration', 'invoice', 'Invoice',
        'OptionalText', 'CongressSelector', 'BankAccount', 'Country', 'Workplace', 'InvoiceUtils', 'registrationRegistrationTypes', 'roomReservations', 'orderedOptionalServices'];

    function InvoiceSetupDialogController($timeout, $scope, $state, $stateParams, registration, invoice, Invoice,
              OptionalText, CongressSelector, BankAccount, Country, Workplace, InvoiceUtils, registrationRegistrationTypes, roomReservations, orderedOptionalServices) {
        var vm = this;
        vm.datePickerOpenStatus = {};
        vm.isShowInvoicePanel = false;
        vm.invoice = invoice;
        vm.bankAccounts = [];
        vm.partners = [];
        vm.registration = registration;
        vm.registrationRegistrationTypes = registrationRegistrationTypes;
        vm.roomReservations = roomReservations;
        vm.orderedOptionalServices = orderedOptionalServices;
        vm.taxNumberPattern = '';
        vm.openCalendar = openCalendar;
        vm.createInvoiceForPrinting = createInvoiceForPrinting;
        vm.setOptionalTextMessage = setOptionalTextMessage;
        vm.selectPartner = selectPartner;
        vm.getRegistrationCurrency = getRegistrationCurrency;
        vm.showConfirmationPanel = showConfirmationPanel;
        vm.printInvoice = printInvoice;
        vm.sendAndPrintInvoice = sendAndPrintInvoice;
        vm.isLastNameRequired = isLastNameRequired;
        vm.isFirstNameRequired = isFirstNameRequired;
        vm.isOptionalNameRequired = isOptionalNameRequired;
        vm.isOptionalNameRequired = isOptionalNameRequired;
        vm.changeBillingMethod = changeBillingMethod;
        vm.setInvoiceName = setInvoiceName;
        vm.navVatCategoryChanged = navVatCategoryChanged;
        vm.isTaxNumberDisabled = isTaxNumberDisabled;
        vm.isTaxNumberRequired = isTaxNumberRequired;


        vm.countries = Country.query();
        OptionalText.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.optionalTexts = result;
        });

        Workplace.queryForCongress({ id: CongressSelector.getSelectedCongress().id }, function(result) {
            vm.partners = result;
        });

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function selectPartner(partner) {
            vm.invoice.name1 = partner.name;
            vm.invoice.vatRegNumber = partner.vatRegNumber;
            vm.invoice.country = partner.country ? partner.country.code : null;
            vm.invoice.zipCode = partner.zipCode;
            vm.invoice.city = partner.city;
            vm.invoice.street = partner.street;
        }

        function setOptionalTextMessage(text) {
            vm.invoice.optionalText = text;
        }

        function getRegistrationCurrency() {
            if (vm.registrationRegistrationTypes.length) {
                return vm.registrationRegistrationTypes[0].chargeableItemCurrency;
            }

            if (vm.roomReservations.length) {
                return vm.roomReservations[0].chargeableItemCurrency;
            }

            if (vm.orderedOptionalServices.length) {
                return vm.orderedOptionalServices[0].chargeableItemCurrency;
            }
            return null;
        }

        InvoiceUtils.hasValidRate(vm.getRegistrationCurrency())
            .then(function (response) {
                if (response.hasValidRate) {
                    vm.hasValidRate = true;
                }
                else {
                    vm.hasValidRate = false;
                }
            })
            .catch(function() {
                vm.hasValidRate = false;
            });

        BankAccount.queryByCongressId({congressId: CongressSelector.getSelectedCongress().id, currency: vm.getRegistrationCurrency() }, function(result) {
            vm.bankAccounts = result;
            if (vm.bankAccounts.length === 1) {
                vm.invoice.bankAccount = vm.bankAccounts[0];
            }
        });

        function showConfirmationPanel() {
            vm.invoiceEmail = vm.registration.email;
            vm.isShowConfirmationPanel = true;
        }

        function onSaveSuccess(result) {
            var pdfLink = '/api/invoices/' + result.id + '/pdf';
            window.open(pdfLink, '_blank');
            $state.transitionTo('invoice', $stateParams, {
                reload: true, inherit: false, notify: true
            });
            //vm.isSaving = false;
        }

        function onSaveError(result) {
            //$state.go('invoice', {id: registration.id}, {reload: true
            $state.transitionTo('invoice', $stateParams, {
                reload: true, inherit: false, notify: true
            });
            vm.isSaving = false;
        }

        function printInvoice() {
            vm.isSaving = true;
            Invoice.save(createInvoiceForPrinting(), onSaveSuccess, onSaveError);
        }

        function sendAndPrintInvoice() {
            vm.isSaving = true;
            Invoice.saveAndSendEmail(createInvoiceForPrinting(), onSaveSuccess, onSaveError);
        }

        function createInvoiceForPrinting() {
            var invoice = {};
            invoice.name1 = vm.invoice.name1;
            invoice.name2 = vm.invoice.name2;
            // invoice.optionalName = vm.invoice.optionalName;
            invoice.vatRegNumber = vm.invoice.vatRegNumber;
            invoice.city = vm.invoice.city;
            invoice.zipCode = vm.invoice.zipCode;
            invoice.street = vm.invoice.street;
            invoice.country = vm.invoice.country;
            invoice.startDate = vm.invoice.startDate;
            invoice.endDate = vm.invoice.endDate;
            invoice.dateOfFulfilment = vm.invoice.dateOfFulfilment;
            invoice.paymentDeadline = vm.invoice.paymentDeadline;
            invoice.billingMethod = vm.invoice.billingMethod;
            invoice.language = vm.invoice.language;
            invoice.navVatCategory = vm.invoice.navVatCategory;
            invoice.optionalText = vm.invoice.optionalText;
            invoice.bankAccount = vm.invoice.bankAccount;
            invoice.registrationId = vm.registration.id;
            invoice.customInvoiceEmail = vm.invoiceEmail;

            // alter ignored items from obj properties to list of numbers
            invoice.ignoredChargeableItemIdList = [];
            for (var prop in vm.invoice.ignoredChargeableItems) {
                if (vm.invoice.ignoredChargeableItems.hasOwnProperty(prop) && vm.invoice.ignoredChargeableItems[prop]) {
                    invoice.ignoredChargeableItemIdList.push(parseInt(prop, 10));
                }
            }

            invoice.ignoredChargedServiceIdList = [];
            for (var prop in vm.invoice.ignoredChargedServices) {
                if (vm.invoice.ignoredChargedServices.hasOwnProperty(prop) && vm.invoice.ignoredChargedServices[prop]) {
                    invoice.ignoredChargedServiceIdList.push(parseInt(prop, 10));
                }
            }

            return invoice;
        }

        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;
        vm.datePickerOpenStatus.paymentDeadline = false;
        vm.datePickerOpenStatus.dateOfFulfilment = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function isLastNameRequired () {
            return vm.invoice.firstName || !vm.invoice.optionalName;
        }

        function isFirstNameRequired () {
            return vm.invoice.lastName || !vm.invoice.optionalName;
        }

        function isOptionalNameRequired () {
            return !vm.invoice.firstName && !vm.invoice.lastName;
        }

        function changeBillingMethod () {
            if (vm.invoice.billingMethod === 'TRANSFER') {
                vm.invoice.dateOfFulfilment = new Date();
                vm.invoice.paymentDeadline = (new Date()).setDate((new Date()).getDate() + 10);
            }
            else {
                vm.invoice.dateOfFulfilment = new Date();
                vm.invoice.paymentDeadline = new Date();
            }
        }

        function setInvoiceName() {
            if (!vm.invoice.invoiceName) {
                if (vm.invoice.language === 'hu') {
                    vm.invoice.name1 = vm.invoice.lastName + ' ' + vm.invoice.firstName;
                }
                else {
                    vm.invoice.name1 = vm.invoice.firstName + ' ' + vm.invoice.lastName;
                }
            }
        }

        function isTaxNumberDisabled() {
            if (vm.invoice.navVatCategory === 'PRIVATE_PERSON') {
                return true;
            }
            else {
                return false;
            }
        }

        function isTaxNumberRequired() {
            if (vm.invoice.navVatCategory === 'DOMESTIC_NORMAL_VAT_TAX_NUMBER' ||
                vm.invoice.navVatCategory === 'DOMESTIC_GROUP_VAT_TAX_NUMBER') {
                return true;
            }
            else {
                return false;
            }
        }

        function navVatCategoryChanged() {
            if (vm.invoice.navVatCategory === 'PRIVATE_PERSON') {
                vm.invoice.vatRegNumber = null;
            }

            if (vm.invoice.navVatCategory === 'DOMESTIC_NORMAL_VAT_TAX_NUMBER') {
                vm.taxNumberPattern = /^[0-9]{8}-[0-9]{1}-[0-9]{2}$/;
            }
            else if (vm.invoice.navVatCategory === 'EU_NO_VAT_EU_TAX_NUMBER' || vm.invoice.navVatCategory === 'EU_VAT_EU_TAX_NUMBER') {
                vm.taxNumberPattern = /^[A-Z]{2}[0-9A-Z]{2,13}$/;
            }
            else {
                vm.taxNumberPattern = '';
            }
        }

    }
})();
