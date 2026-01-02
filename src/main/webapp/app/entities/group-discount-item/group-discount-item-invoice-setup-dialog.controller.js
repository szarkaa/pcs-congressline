(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountItemInvoiceSetupDialogController', GroupDiscountItemInvoiceSetupDialogController);

    GroupDiscountItemInvoiceSetupDialogController.$inject = ['$timeout', '$scope', '$state', '$stateParams', 'invoice', 'GroupDiscountInvoice', 'groupDiscountItemFilter',
        'OptionalText', 'CongressSelector', 'BankAccount', 'Country', 'InvoiceUtils', 'GroupDiscountItemFilter'];

    function GroupDiscountItemInvoiceSetupDialogController($timeout, $scope, $state, $stateParams, invoice, GroupDiscountInvoice, groupDiscountItemFilter,
              OptionalText, CongressSelector, BankAccount, Country, InvoiceUtils, GroupDiscountItemFilter) {
        var vm = this;

        vm.invoice = invoice;
        vm.invoice.payingGroup = groupDiscountItemFilter.payingGroup;
        vm.selectedChargeableItemIds = groupDiscountItemFilter.selectedChargeableItemIds;
        if (!vm.invoice.payingGroup) {
            $state.go('group-discount-item', null, {refresh: 'group-discount-item'});
        }

        vm.datePickerOpenStatus = {};
        vm.isShowInvoicePanel = false;
        vm.printInvoice = printInvoice;
        vm.bankAccounts = [];
        vm.taxNumberPattern = '';

        vm.openCalendar = openCalendar;
        vm.createInvoiceForPrinting = createInvoiceForPrinting;
        vm.setOptionalTextMessage = setOptionalTextMessage;
        vm.showConfirmationPanel = showConfirmationPanel;
        vm.sendAndPrintInvoice = sendAndPrintInvoice;
        vm.isAnyItemSelectedForInvoicing = isAnyItemSelectedForInvoicing;
        vm.changeBillingMethod = changeBillingMethod;
        vm.navVatCategoryChanged = navVatCategoryChanged;
        vm.isTaxNumberDisabled = isTaxNumberDisabled;
        vm.isTaxNumberRequired = isTaxNumberRequired;

        vm.countries = Country.query();
        OptionalText.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.optionalTexts = result;
        });

        function setOptionalTextMessage(text) {
            vm.invoice.optionalText = text;
        }

        InvoiceUtils.hasValidRate(vm.invoice.payingGroup.currency)
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

        BankAccount.queryByCongressId({congressId: CongressSelector.getSelectedCongress().id, currency: vm.invoice.payingGroup.currency.currency }, function(result) {
            vm.bankAccounts = result;
            if (vm.bankAccounts.length == 1) {
                vm.invoice.bankAccount = vm.bankAccounts[0];
            }
        });

        function showConfirmationPanel() {
            vm.invoiceEmail = vm.invoice.payingGroup.email;
            vm.isShowConfirmationPanel = true;
        }

        function onSaveSuccess(result) {
            GroupDiscountItemFilter.resetSelectedItems();
            var pdfLink = '/api/group-discount-invoices/' + result.id + '/pdf';
            window.open(pdfLink, '_blank');
            $state.transitionTo('group-discount-item', $stateParams, {
                reload: true, inherit: false, notify: true
            });
            //vm.isSaving = false;
        }

        function onSaveError(result) {
            $state.transitionTo('group-discount-item', $stateParams, {
                reload: true, inherit: false, notify: true
            });
            vm.isSaving = false;
        }

        function printInvoice() {
            vm.isSaving = true;
            GroupDiscountInvoice.save(createInvoiceForPrinting(), onSaveSuccess, onSaveError);
        }

        function sendAndPrintInvoice() {
            vm.isSaving = true;
            GroupDiscountInvoice.saveAndSendEmail(createInvoiceForPrinting(), onSaveSuccess, onSaveError);
        }

        function createInvoiceForPrinting() {
            var invoice = {};
            invoice.name = vm.invoice.name;
            invoice.taxNumber = vm.invoice.taxNumber;
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
            invoice.payingGroup = vm.invoice.payingGroup;
            invoice.customInvoiceEmail = vm.invoiceEmail;

            // alter ignored items from obj properties to list of numbers
            invoice.chargeableItemIdList = [];
            for (var prop in vm.selectedChargeableItemIds) {
                if (vm.selectedChargeableItemIds.hasOwnProperty(prop) && vm.selectedChargeableItemIds[prop]) {
                    invoice.chargeableItemIdList.push(parseInt(prop, 10));
                }
            }
            return invoice;
        }

        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;
        vm.datePickerOpenStatus.dateOfFulfilment = false;
        vm.datePickerOpenStatus.paymentDeadline = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function isAnyItemSelectedForInvoicing () {
            for (var prop in vm.selectedChargeableItemIds) {
                if (vm.selectedChargeableItemIds.hasOwnProperty(prop)) {
                    return true;
                }
            }
            return false;
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
