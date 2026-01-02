(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscInvoiceItemController', MiscInvoiceItemController);

    MiscInvoiceItemController.$inject = ['$scope', '$state', 'miscInvoice', 'selectedCurrency'];

    function MiscInvoiceItemController ($scope, $state, miscInvoice, selectedCurrency) {
        var vm = this;
        vm.miscInvoiceItems = miscInvoice.miscInvoiceItems;
        vm.selectedCurrency = selectedCurrency;
    }
})();
