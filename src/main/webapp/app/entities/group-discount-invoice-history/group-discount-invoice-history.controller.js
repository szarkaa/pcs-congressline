(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountInvoiceHistoryController', GroupDiscountInvoiceHistoryController);

    GroupDiscountInvoiceHistoryController.$inject = ['$scope', '$state', 'GroupDiscountInvoiceHistory'];

    function GroupDiscountInvoiceHistoryController ($scope, $state, GroupDiscountInvoiceHistory) {
        var vm = this;
        
        vm.groupDiscountInvoiceHistories = [];

        loadAll();

        function loadAll() {
            GroupDiscountInvoiceHistory.query(function(result) {
                vm.groupDiscountInvoiceHistories = result;
            });
        }
    }
})();
