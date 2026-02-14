(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountItemController', GroupDiscountItemController);

    GroupDiscountItemController.$inject = ['$scope', '$state', 'GroupDiscountItem', 'DTOptionsBuilder', 'DTColumnDefBuilder'
        , 'CongressSelector', 'groupDiscountItemFilter', 'Rate', 'GroupDiscountPayment'];

    function GroupDiscountItemController ($scope, $state, GroupDiscountItem, DTOptionsBuilder, DTColumnDefBuilder
                                          , CongressSelector, groupDiscountItemFilter, Rate, GroupDiscountPayment) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [2, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).notSortable()
        ];

        vm.allChargeableItemIdSelected = false;
        vm.toggleGroupDiscountSelection = toggleGroupDiscountSelection;
        vm.toggleAllGroupDiscountSelection = toggleAllGroupDiscountSelection;
        vm.groupDiscountItemFilter = groupDiscountItemFilter;
        vm.isAnyChargeableItemIdSelected = isAnyChargeableItemIdSelected;
        vm.groupDiscountItems = [];
        vm.groupDiscountPayments = [];
        vm.sumRegistration = 0;
        vm.sumHotel = 0;
        vm.sumOptionalService = 0;
        vm.groupPaidRegistration = 0;
        vm.groupPaid = 0;
        vm.groupPaidOptionalService = 0;

        vm.downloadReportXls = downloadReportXls;
        vm.downloadProFormaGroupInvoiceReportXls = downloadProFormaGroupInvoiceReportXls;
        vm.isSelectedPayingGroupInvoiceable = isSelectedPayingGroupInvoiceable;
        vm.hasValidCurrencyRate = true;

        checkValidCurrencyRate();
        loadAll();

        function loadAll() {
            if (vm.groupDiscountItemFilter.payingGroup) {
                if (vm.groupDiscountItemFilter.chargeableItemType !== 'ALL') {
                    GroupDiscountItem.query({
                            meetingCode: CongressSelector.getSelectedCongress().meetingCode,
                            payingGroupId: groupDiscountItemFilter.payingGroup.id,
                            chargeableItemType: groupDiscountItemFilter.chargeableItemType
                        },
                        function (result) {
                            vm.groupDiscountItems = result;
                            calculateSumAmountByChargeableItemType();
                        }
                    );
                }
                else {
                    GroupDiscountItem.query({
                            meetingCode: CongressSelector.getSelectedCongress().meetingCode,
                            payingGroupId: groupDiscountItemFilter.payingGroup.id
                        },
                        function (result) {
                            vm.groupDiscountItems = result;
                            calculateSumAmountByChargeableItemType();
                        }
                    );
                }

                GroupDiscountPayment.queryByPayingGroupId({id: groupDiscountItemFilter.payingGroup.id}, function(result) {
                    vm.groupDiscountPayments = result;
                    calculateGroupPaidAmountByChargeableItemType();
                });
            }
        }

        function toggleGroupDiscountSelection(chargeableItemId) {
            // vm.groupDiscountItemFilter.selectedChargeableItemIds[chargeableItemId.toString()] = !isChargeableItemIdSelected(chargeableItemId);
        }

        function toggleAllGroupDiscountSelection() {
            if (!vm.allChargeableItemIdSelected) {
                vm.groupDiscountItemFilter.selectedChargeableItemIds = {};
            }
            else {
                for (var i = 0; i < vm.groupDiscountItems.length; i++) {
                    if (!vm.groupDiscountItems[i].invoiceNumber) {
                        vm.groupDiscountItemFilter.selectedChargeableItemIds[vm.groupDiscountItems[i].chargeableItemId.toString()] = true;
                    }
                }
            }
        }

        function isChargeableItemIdSelected(chargeableItemId) {
            for (var prop in vm.groupDiscountItemFilter.selectedChargeableItemIds) {
                if (vm.groupDiscountItemFilter.selectedChargeableItemIds.hasOwnProperty(prop) && prop === chargeableItemId) {
                    return true;
                }
            }
            return false;
        }

        function isAnyChargeableItemIdSelected() {
            for (var prop in vm.groupDiscountItemFilter.selectedChargeableItemIds) {
                if (vm.groupDiscountItemFilter.selectedChargeableItemIds.hasOwnProperty(prop) && vm.groupDiscountItemFilter.selectedChargeableItemIds[prop]) {
                    return true;
                }
            }
            return false;
        }

        function isSelectedPayingGroupInvoiceable() {
            if (groupDiscountItemFilter.payingGroup && groupDiscountItemFilter.payingGroup.currency
                && groupDiscountItemFilter.payingGroup.currency.currency === 'HUF' && !groupDiscountItemFilter.payingGroup.taxNumber) {
                return false;
            }
            return true;
        }

        function calculateSumAmountByChargeableItemType() {
            vm.sumRegistration = 0;
            vm.sumHotel = 0;
            vm.sumOptionalService = 0;
            for (var i = 0; i < vm.groupDiscountItems.length; i++) {
                var item = vm.groupDiscountItems[i];
                switch (item.chargeableItemType) {
                    case "REGISTRATION":
                        vm.sumRegistration += item.amount;
                        break;
                    case "HOTEL":
                        vm.sumHotel += item.amount;
                        break;
                    case "OPTIONAL_SERVICE":
                        vm.sumOptionalService += item.amount;
                        break;
                }
            }
        }

        function calculateGroupPaidAmountByChargeableItemType() {
            vm.groupPaidRegistration = 0;
            vm.groupPaidHotel = 0;
            vm.groupPaidOptionalService = 0;
            for (var i = 0; i < vm.groupDiscountPayments.length; i++) {
                var item = vm.groupDiscountPayments[i];
                if (vm.groupDiscountItemFilter.payingGroup && vm.groupDiscountItemFilter.payingGroup.currency.currency === item.currency.currency) {
                    switch (item.paymentType) {
                        case "REGISTRATION":
                            vm.groupPaidRegistration += item.amount;
                            break;
                        case "HOTEL":
                            vm.groupPaidHotel += item.amount;
                            break;
                        case "OPTIONAL_SERVICE":
                            vm.groupPaidOptionalService += item.amount;
                            break;
                    }
                }
            }
        }

        function checkValidCurrencyRate() {
            var currency;
            if (vm.groupDiscountItemFilter && vm.groupDiscountItemFilter.payingGroup) {
                currency = vm.groupDiscountItemFilter.payingGroup.currency.currency;
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

        function downloadReportXls () {
            window.location.href = '/api/group-discount-items/' +
                CongressSelector.getSelectedCongress().meetingCode + '/' +
                groupDiscountItemFilter.payingGroup.id + '/' +
                groupDiscountItemFilter.chargeableItemType + '/' +
                'download-report';
        }

        function downloadProFormaGroupInvoiceReportXls () {
            window.location.href = '/api/group-discount-items/' +
                CongressSelector.getSelectedCongress().meetingCode + '/' +
                groupDiscountItemFilter.payingGroup.id + '/' +
                groupDiscountItemFilter.chargeableItemType + '/' +
                'download-pro-forma-group-invoice-report';
        }

    }
})();
