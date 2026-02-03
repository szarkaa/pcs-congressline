(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscInvoiceSetupDialogController', MiscInvoiceSetupDialogController);

    MiscInvoiceSetupDialogController.$inject = ['$scope', '$state', '$stateParams', 'miscInvoice', 'MiscInvoice', 'Country',
        'OptionalText', 'CongressSelector', 'BankAccount', 'InvoiceUtils', 'Workplace'];

    function MiscInvoiceSetupDialogController ($scope, $state, $stateParams, miscInvoice, MiscInvoice, Country, OptionalText, CongressSelector, BankAccount, InvoiceUtils, Workplace) {
        var vm = this;
        vm.isShowInvoicePanel = false;
        vm.datePickerOpenStatus = {};
        vm.invoice = miscInvoice;
        vm.optionalTexts = [];
        vm.bankAccounts = [];
        vm.partners = [];
        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;
        vm.datePickerOpenStatus.dateOfFulfilment = false;
        vm.datePickerOpenStatus.paymentDeadline = false;
        vm.taxNumberPattern = '';

        vm.openCalendar = openCalendar;
        vm.setOptionalTextMessage = setOptionalTextMessage;
        vm.clear = clear;
        vm.showConfirmationPanel = showConfirmationPanel;
        vm.sendAndPrintInvoice = sendAndPrintInvoice;
        vm.printInvoice = printInvoice;
        vm.getCurrency = getCurrency;
        vm.selectPartner = selectPartner;
        vm.changeBillingMethod = changeBillingMethod;
        vm.navVatCategoryChanged = navVatCategoryChanged;
        vm.invoiceTypeChanged = invoiceTypeChanged;
        vm.isTaxNumberDisabled = isTaxNumberDisabled;
        vm.isTaxNumberRequired = isTaxNumberRequired;


        vm.countries = Country.query();
        Workplace.queryForCongress({ id: CongressSelector.getSelectedCongress().id }, function(result) {
            vm.partners = result;
        });

        OptionalText.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.optionalTexts = result;
        });

        function setOptionalTextMessage(text) {
            vm.invoice.optionalText = text;
        }

        function selectPartner(partner) {
            vm.invoice.savePartner = false;
            vm.invoice.name1 = partner.name;
            vm.invoice.vatRegNumber = partner.vatRegNumber;
            vm.invoice.country = partner.country ? partner.country.code : '';
            vm.invoice.department = partner.department;
            vm.invoice.zipCode = partner.zipCode;
            vm.invoice.city = partner.city;
            vm.invoice.street = partner.street;
            vm.invoice.phone = partner.phone;
            vm.invoice.fax = partner.fax;
            vm.invoice.email = partner.email;
        }

        function getCurrency() {
            if (vm.invoice.miscInvoiceItems.length) {
                return vm.invoice.miscInvoiceItems[0].miscService.currency.currency;
            }
            return null;
        }

        InvoiceUtils.hasValidRate(vm.getCurrency())
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


        BankAccount.queryByCongressId({congressId: CongressSelector.getSelectedCongress().id, currency: vm.getCurrency() }, function(result) {
            vm.bankAccounts = result;
            if (vm.bankAccounts.length === 1) {
                vm.invoice.bankAccountId = vm.bankAccounts[0].id;
            }
        });

        function clear() {
            vm.invoice.invoiceType = null;
            vm.invoice.name1 = null;
            vm.invoice.name2 = null;
            vm.invoice.name3 = null;
            vm.invoice.vatRegNumber = null;
            vm.invoice.street = null;
            vm.invoice.city = null;
            vm.invoice.zipCode = null;
            vm.invoice.country = null;
            vm.invoice.optionalText = null;
            vm.invoice.startDate = new Date(CongressSelector.getSelectedCongress().startDate);
            vm.invoice.endDate = new Date(CongressSelector.getSelectedCongress().endDate);
            vm.invoice.dateOfFulfilment = new Date();
            vm.invoice.paymentDeadline = (new Date()).setDate((new Date()).getDate() + 10);
            vm.invoice.billingMethod = 'TRANSFER';
            vm.invoice.language = 'hu';
            vm.invoice.bankAccountId = null;
        }

        function showConfirmationPanel() {
            vm.invoiceEmail = "";
            vm.isShowConfirmationPanel = true;
        }

        function onSaveSuccess(result) {
            var pdfLink = '/api/misc-invoices/' + result.invoiceCongressId + '/pdf';
            window.open(pdfLink, '_blank');
            $state.go('misc-invoice', {}, {reload: true, notify: true});
        }

        function onSaveError (result) {
            $state.go('misc-invoice', {}, {reload: true, notify: true});
            vm.isSaving = false;
        }

        function printInvoice() {
            vm.isSaving = true;
            transformInvoice()
            MiscInvoice.save(vm.invoice, onSaveSuccess, onSaveError);
        }

        function sendAndPrintInvoice () {
            vm.isSaving = true;
            transformInvoice();
            MiscInvoice.saveAndSendEmail(vm.invoice, onSaveSuccess, onSaveError);
        }

        function transformInvoice() {
            for (var i = 0; i < vm.invoice.miscInvoiceItems.length; i++) {
                vm.invoice.miscInvoiceItems[i].miscServiceId = vm.invoice.miscInvoiceItems[i].miscService.id;
                delete vm.invoice.miscInvoiceItems[i].miscService;
                delete vm.invoice.miscInvoiceItems[i].id;
            }
        }

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function changeBillingMethod () {
            if (vm.invoice.billingMethod === 'TRANSFER') {
                vm.invoice.dateOfFulfilment = vm.invoice.startDate;
                vm.invoice.paymentDeadline = (new Date()).setDate((new Date()).getDate() + 10);
            }
            else {
                vm.invoice.dateOfFulfilment = new Date();
                vm.invoice.paymentDeadline = new Date();
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

        function invoiceTypeChanged() {
            if (vm.invoice.invoiceType === 'PRO_FORMA') {
                vm.invoice.navVatCategory = 'DOMESTIC_NORMAL_VAT_TAX_NUMBER';
            }
            else {
                vm.invoice.navVatCategory = null;
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
