(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountInvoiceController', GroupDiscountInvoiceController);

    GroupDiscountInvoiceController.$inject = ['$scope', '$state', 'GroupDiscountInvoice', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector'];

    function GroupDiscountInvoiceController ($scope, $state, GroupDiscountInvoice, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [0, 'desc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.downloadPdf = downloadPdf;
        vm.downloadXls = downloadXls;

        vm.groupDiscountInvoices = [];

        loadAll();

        function downloadPdf(invoiceId) {
            window.open('/api/group-discount-invoices/' + invoiceId + '/pdf', '_blank');
        }

        function downloadXls(invoiceId) {
            window.open('/api/group-discount-invoices/' + invoiceId + '/details/xls', '_blank');
        }

        function loadAll() {
            GroupDiscountInvoice.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                vm.groupDiscountInvoices = result;
            });
        }
    }
})();
