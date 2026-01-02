(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscInvoiceController', MiscInvoiceController);

    MiscInvoiceController.$inject = ['$scope', '$state', 'MiscInvoice', 'CongressSelector', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function MiscInvoiceController ($scope, $state, MiscInvoice, CongressSelector, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', true);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.miscInvoices = [];

        loadAll();

        vm.downloadPdf = function (invoiceId) {
            //var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
            window.open('/api/misc-invoices/' + invoiceId + '/pdf', '_blank');
        };

        function loadAll() {
            MiscInvoice.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                vm.miscInvoices = result;
            });
        }
    }
})();
