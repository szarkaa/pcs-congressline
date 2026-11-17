(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscInvoiceController', MiscInvoiceController);

    MiscInvoiceController.$inject = ['$scope', '$state', 'MiscInvoice', 'CongressSelector', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function MiscInvoiceController ($scope, $state, MiscInvoice, CongressSelector, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', true);
        vm.dtOptions.withOption('order', [1, 'desc']);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable(),
            DTColumnDefBuilder.newColumnDef(0).notSortable()
        ];

        vm.miscInvoices = [];
        vm.selectedMiscInvoiceIds = {};
        vm.allMiscInvoiceIdSelected = false;


        loadAll();

        vm.toggleMiscInvoiceIdSelection = toggleMiscInvoiceIdSelection;
        vm.toggleAllMiscInvoiceIdSelection = toggleAllMiscInvoiceIdSelection;
        vm.isAnyMiscInvoiceIdSelected = isAnyMiscInvoiceIdSelected;
        vm.getSelectedMiscInvoiceIds = getSelectedMiscInvoiceIds;


        vm.downloadPdf = function (invoiceId) {
            //var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
            window.open('/api/misc-invoices/' + invoiceId + '/pdf', '_blank');
        };

        function loadAll() {
            MiscInvoice.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                vm.miscInvoices = result;
            });
        }

        function toggleMiscInvoiceIdSelection(regId) {
            // vm.selectedMiscInvoiceIds[regId.toString()] = !isOnlineMiscInvoiceIdSelected(regId);
        }

        function getSelectedMiscInvoiceIds() {
            var regIdList = [];
            for (var prop in vm.selectedMiscInvoiceIds) {
                if (vm.selectedMiscInvoiceIds.hasOwnProperty(prop) && vm.selectedMiscInvoiceIds[prop]) {
                    regIdList.push(parseInt(prop, 10));
                }
            }
            return regIdList;
        }

        function clearMiscInvoiceIdSelection() {
            vm.selectedMiscInvoiceIds = {};
            vm.allMiscInvoiceIdSelected = false;
        }

        function toggleAllMiscInvoiceIdSelection() {
            if (!vm.allMiscInvoiceIdSelected) {
                vm.selectedMiscInvoiceIds = {};
            }
            else {
                vm.selectedMiscInvoiceIds = {};
                for (var i = 0; i < vm.miscInvoices.length; i++) {
                    vm.selectedMiscInvoiceIds[vm.miscInvoices[i].invoiceCongressId.toString()] = true;
                }
            }
        }

        function isMiscInvoiceIdSelected(regId) {
            for (var prop in vm.selectedMiscInvoiceIds) {
                if (vm.selectedMiscInvoiceIds.hasOwnProperty(prop) && prop === regId) {
                    return true;
                }
            }
            return false;
        }

        function isAnyMiscInvoiceIdSelected() {
            for (var prop in vm.selectedMiscInvoiceIds) {
                if (vm.selectedMiscInvoiceIds.hasOwnProperty(prop) && vm.selectedMiscInvoiceIds[prop]) {
                    return true;
                }
            }
            return false;
        }
    }
})();
